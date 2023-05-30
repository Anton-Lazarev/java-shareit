package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.IncomeBookingDto;
import ru.practicum.shareit.booking.dto.OutcomeBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectBookingApproverException;
import ru.practicum.shareit.exceptions.IncorrectOwnerInBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public OutcomeBookingDto addBooking(int userID, IncomeBookingDto bookingDto) {
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
    public OutcomeBookingDto changeBookingStatus(int userID, int bookingID, boolean approve) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!bookingRepository.existsById(bookingID)) {
            throw new BookingNotFoundException("Booking with ID " + bookingID + " not presented");
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
        log.info("Change status of booking with ID {} on {}", bookingID, booking.getStatus().name());
        return BookingMapper.bookingToOutcomeBookingDTO(bookingRepository.save(booking));
    }

    @Override
    public OutcomeBookingDto getBookingByID(int userID, int bookingID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!bookingRepository.existsById(bookingID)) {
            throw new BookingNotFoundException("Booking with ID " + bookingID + " not presented");
        }
        Booking booking = bookingRepository.findById(bookingID).get();
        if (booking.getBooker().getId() != userID && booking.getItem().getOwner().getId() != userID) {
            throw new IncorrectOwnerInBookingException("User with ID " + userID + " not item or booking owner");
        }
        log.info("Getting booking with ID {} on request from user with ID {}", booking, userID);
        return BookingMapper.bookingToOutcomeBookingDTO(booking);
    }

    @Override
    public List<OutcomeBookingDto> getBookingsOfUserByState(int userID, String state) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<Booking> bookings;
        List<OutcomeBookingDto> dtos;
        switch (state.toUpperCase()) {
            case ("CURRENT"):
                bookings = bookingRepository.findBookingsOfUserInStateCURRENT(userID, LocalDateTime.now());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingsOfUserInStatePAST(userID, LocalDateTime.now());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingsOfUserInStateFUTURE(userID, LocalDateTime.now());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingsOfUserInStateWAITING(userID, LocalDateTime.now());
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingsOfUserInStateREJECTED(userID);
                break;
            default:
                bookings = bookingRepository.findBookingsOfUserInStateALL(userID);
        }
        dtos = bookings.stream().map(BookingMapper::bookingToOutcomeBookingDTO)
                .collect(Collectors.toList());
        log.info("Get bookingDTO list with size {} of bookings for user with ID {} and state {}",
                dtos.size(), userID, state);
        return dtos;
    }

    @Override
    public List<OutcomeBookingDto> getBookingsOfUserItemsByState(int userID, String state) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<Booking> bookings;
        List<OutcomeBookingDto> dtos;
        switch (state.toUpperCase()) {
            case ("CURRENT"):
                bookings = bookingRepository.findBookingsOfItemOwnerInStateCURRENT(userID, LocalDateTime.now());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingsOfItemOwnerInStatePAST(userID, LocalDateTime.now());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingsOfItemOwnerInStateFUTURE(userID, LocalDateTime.now());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingsOfItemOwnerInStateWAITING(userID, LocalDateTime.now());
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingsOfItemOwnerInStateREJECTED(userID);
                break;
            default:
                bookings = bookingRepository.findBookingsOfItemOwnerInStateALL(userID);
        }
        dtos = bookings.stream().map(BookingMapper::bookingToOutcomeBookingDTO)
                .collect(Collectors.toList());
        log.info("Get bookingDTO list with size {} of bookings for item owner with ID {} and state {}",
                dtos.size(), userID, state);
        return dtos;
    }
}
