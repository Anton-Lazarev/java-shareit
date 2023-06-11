package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Paginator;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStateRequest;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectBookingApproverException;
import ru.practicum.shareit.exceptions.IncorrectOwnerInBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public OutcomeBookingDTO addBooking(int userID, IncomeBookingDTO bookingDto) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new ItemNotFoundException("Item with ID " + bookingDto.getItemId() + " not present");
        }
        Item itemForBooking = itemRepository.findById(bookingDto.getItemId()).get();
        if (!itemForBooking.getAvailable()) {
            throw new BookingValidationException("Item with ID " + itemForBooking.getId() + " unavailable for booking");
        }
        Booking newBooking = BookingMapper.incomeBookingDtoToBooking(bookingDto,
                userRepository.findById(userID).get(),
                itemForBooking);
        BookingValidator.validate(newBooking);
        log.info("Add booking from user with ID {} for item with ID {}", userID, itemForBooking.getId());
        bookingRepository.save(newBooking);
        return BookingMapper.bookingToOutcomeBookingDTO(newBooking);
    }

    @Override
    @Transactional
    public OutcomeBookingDTO changeBookingStatus(int userID, int bookingID, boolean approve) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!bookingRepository.existsById(bookingID)) {
            throw new BookingNotFoundException("Booking with ID " + bookingID + " not present");
        }
        Booking booking = bookingRepository.findById(bookingID).get();
        if (userID != booking.getItem().getOwner().getId()) {
            throw new IncorrectBookingApproverException("User with ID " + userID + " not owner of booked item");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingValidationException("Booking with ID " + bookingID + " already approved");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Change status of booking with ID {} on {}", bookingID, booking.getStatus().name());
        return BookingMapper.bookingToOutcomeBookingDTO(booking);
    }

    @Override
    public OutcomeBookingDTO getBookingByID(int userID, int bookingID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!bookingRepository.existsById(bookingID)) {
            throw new BookingNotFoundException("Booking with ID " + bookingID + " not present");
        }
        Booking booking = bookingRepository.findById(bookingID).get();
        if (booking.getBooker().getId() != userID && booking.getItem().getOwner().getId() != userID) {
            throw new IncorrectOwnerInBookingException("User with ID " + userID + " not item or booking owner");
        }
        log.info("Getting booking with ID {} on request from user with ID {}", booking, userID);
        return BookingMapper.bookingToOutcomeBookingDTO(booking);
    }

    @Override
    public List<OutcomeBookingDTO> getBookingsOfUserByState(int userID, String state, int from, int size) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Paginator paginator = new Paginator(from, size);
        List<Booking> bookings;
        List<OutcomeBookingDTO> dtos;
        BookingStateRequest bookingState = BookingStateRequest.valueOf(state.toUpperCase());
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findBookingsOfUserInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsOfUserInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsOfUserInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsOfUserInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsOfUserInStateREJECTED(userID, paginator);
                break;
            case ALL:
                bookings = bookingRepository.findBookingsOfUserInStateALL(userID, paginator);
                break;
            default:
                bookings = Collections.emptyList();
        }
        dtos = bookings.stream().map(BookingMapper::bookingToOutcomeBookingDTO)
                .collect(Collectors.toList());
        log.info("Get bookingDTO list with size {} of bookings for user with ID {} and state {}",
                dtos.size(), userID, state);
        return dtos;
    }

    @Override
    public List<OutcomeBookingDTO> getBookingsOfUserItemsByState(int userID, String state, int from, int size) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Paginator paginator = new Paginator(from, size);
        List<Booking> bookings;
        List<OutcomeBookingDTO> dtos;
        BookingStateRequest bookingState = BookingStateRequest.valueOf(state.toUpperCase());
        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsOfItemOwnerInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateREJECTED(userID, paginator);
                break;
            case ALL:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateALL(userID, paginator);
                break;
            default:
                bookings = Collections.emptyList();
        }
        dtos = bookings.stream().map(BookingMapper::bookingToOutcomeBookingDTO)
                .collect(Collectors.toList());
        log.info("Get bookingDTO list with size {} of bookings for item owner with ID {} and state {}",
                dtos.size(), userID, state);
        return dtos;
    }
}
