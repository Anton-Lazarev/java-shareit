package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.Paginator;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectBookingApproverException;
import ru.practicum.shareit.exceptions.IncorrectOwnerInBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl service;

    @Test
    void addBooking_exception_whenUserNotPresent() {
        int userID = 5;
        int itemID = 12;
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).itemId(itemID).build();

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.addBooking(userID, incomeDTO));

        assertEquals("User with ID 5 not present", exception.getMessage());
    }

    @Test
    void addBooking_exception_whenItemNotPresent() {
        int userID = 11;
        int itemID = 54;
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).itemId(itemID).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.existsById(itemID)).thenReturn(false);
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> service.addBooking(userID, incomeDTO));

        assertEquals("Item with ID 54 not present", exception.getMessage());
    }

    @Test
    void addBooking_exception_whenItemNotAvailable() {
        int userID = 11;
        int itemID = 54;
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).owner(owner).name("dollar").description("one dollar").available(false).build();
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).itemId(itemID).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        BookingValidationException exception = assertThrows(BookingValidationException.class,
                () -> service.addBooking(userID, incomeDTO));

        assertEquals("Item with ID 54 unavailable for booking", exception.getMessage());
    }

    @Test
    void addBooking_exception_whenInvalidBooker() {
        int userID = 11;
        int itemID = 54;
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).owner(owner).name("dollar").description("one dollar").available(true).build();
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).itemId(itemID).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(userRepository.findById(userID)).thenReturn(Optional.of(owner));
        IncorrectOwnerInBookingException exception = assertThrows(IncorrectOwnerInBookingException.class,
                () -> service.addBooking(userID, incomeDTO));

        assertEquals("Owner of item cannot book own item", exception.getMessage());
    }

    @Test
    void addBooking_correctAdding() {
        int userID = 83;
        int itemID = 54;
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).owner(owner).name("dollar").description("one dollar").available(true).build();
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).itemId(itemID).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(userRepository.findById(userID)).thenReturn(Optional.of(booker));
        OutcomeBookingDTO dto = service.addBooking(userID, incomeDTO);

        assertEquals(incomeDTO.getStart(), dto.getStart());
        assertEquals(incomeDTO.getEnd(), dto.getEnd());
        assertEquals(booker.getName(), dto.getBooker().getName());
        assertEquals(item.getName(), dto.getItem().getName());
        assertEquals(dto.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void changeBookingStatus_exception_whenUserNotPresent() {
        int userID = 43;
        int bookingID = 12;

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.changeBookingStatus(userID, bookingID, true));

        assertEquals("User with ID 43 not present", exception.getMessage());
    }

    @Test
    void changeBookingStatus_exception_whenBookingNotPresent() {
        int userID = 45;
        int bookingID = 86;

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(false);
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> service.changeBookingStatus(userID, bookingID, true));

        assertEquals("Booking with ID 86 not present", exception.getMessage());
    }

    @Test
    void changeBookingStatus_exception_whenUserNotItemOwner() {
        int userID = 45;
        int bookingID = 86;
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        IncorrectBookingApproverException exception = assertThrows(IncorrectBookingApproverException.class,
                () -> service.changeBookingStatus(userID, bookingID, true));

        assertEquals("User with ID 45 not owner of booked item", exception.getMessage());
    }

    @Test
    void changeBookingStatus_exception_whenBookingAlreadyApproved() {
        int userID = 45;
        int bookingID = 86;
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(96).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        BookingValidationException exception = assertThrows(BookingValidationException.class,
                () -> service.changeBookingStatus(userID, bookingID, true));

        assertEquals("Booking with ID 86 already approved", exception.getMessage());
    }

    @Test
    void changeBookingStatus_correctApproved() {
        int userID = 45;
        int bookingID = 86;
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(96).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        OutcomeBookingDTO dto = service.changeBookingStatus(userID, bookingID, true);

        assertEquals(dto.getStatus(), BookingStatus.APPROVED);
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getBooker().getName(), dto.getBooker().getName());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
    }

    @Test
    void changeBookingStatus_correctRejected() {
        int userID = 45;
        int bookingID = 86;
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(96).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        OutcomeBookingDTO dto = service.changeBookingStatus(userID, bookingID, false);

        assertEquals(dto.getStatus(), BookingStatus.REJECTED);
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getBooker().getName(), dto.getBooker().getName());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
    }

    @Test
    void getBookingByID_exception_whenUserNotPresent() {
        int userID = 123;
        int bookingID = 826;

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getBookingByID(userID, bookingID));

        assertEquals("User with ID 123 not present", exception.getMessage());
    }

    @Test
    void getBookingByID_exception_whenBookingNotPresent() {
        int userID = 123;
        int bookingID = 826;

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(false);
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> service.getBookingByID(userID, bookingID));

        assertEquals("Booking with ID 826 not present", exception.getMessage());
    }

    @Test
    void getBookingByID_exception_whenUserNotItemOwner() {
        int userID = 475;
        int bookingID = 315;
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(174).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        IncorrectOwnerInBookingException exception = assertThrows(IncorrectOwnerInBookingException.class,
                () -> service.getBookingByID(userID, bookingID));

        assertEquals("User with ID 475 not item or booking owner", exception.getMessage());
    }

    @Test
    void getBookingByID_correctGetting() {
        int userID = 475;
        int bookingID = 315;
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(bookingID)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.existsById(bookingID)).thenReturn(true);
        when(bookingRepository.findById(bookingID)).thenReturn(Optional.of(booking));
        OutcomeBookingDTO dto = service.getBookingByID(userID, bookingID);

        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getBooker().getName(), dto.getBooker().getName());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
    }

    @Test
    void getBookingsOfUserByState_exception_whenUserNotPresent() {
        int userID = 741;
        int from = 0;
        int size = 5;
        String state = "CURRENT";

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getBookingsOfUserByState(userID, state, from, size));

        assertEquals("User with ID 741 not present", exception.getMessage());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStateCURRENT() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "CURRENT";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStatePAST() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "PAST";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStateFUTURE() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "FUTURE";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStateWAITING() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "WAITING";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStateREJECTED() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "REJECTED";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.CANCELED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStateREJECTED(userID, paginator)).thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStateREJECTED(userID, paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserByState_correctGettingStateALL() {
        int userID = 741;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "ALL";
        User owner = User.builder().id(74).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfUserInStateALL(userID, paginator)).thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfUserInStateALL(userID, paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_exception_whenUserNotPresent() {
        int userID = 555;
        int from = 0;
        int size = 5;
        String state = "CURRENT";

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getBookingsOfUserItemsByState(userID, state, from, size));

        assertEquals("User with ID 555 not present", exception.getMessage());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStateCURRENT() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "CURRENT";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStateCURRENT(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStatePAST() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "PAST";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStatePAST(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStateFUTURE() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "FUTURE";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStateFUTURE(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStateWAITING() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "WAITING";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStateWAITING(userID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStateREJECTED() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "REJECTED";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.CANCELED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStateREJECTED(userID, paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStateREJECTED(userID, paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }

    @Test
    void getBookingsOfUserItemsByState_correctGettingStateALL() {
        int userID = 555;
        int from = 0;
        int size = 5;
        Pageable paginator = new Paginator(from, size);
        String state = "ALL";
        User owner = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        User booker = User.builder().id(417).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(63).owner(owner).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder()
                .id(14)
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findBookingsOfItemOwnerInStateALL(userID, paginator))
                .thenReturn(List.of(booking));
        List<OutcomeBookingDTO> dtos = service.getBookingsOfUserItemsByState(userID, state, from, size);

        verify(bookingRepository, atMostOnce()).findBookingsOfItemOwnerInStateALL(userID, paginator);
        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
        assertEquals(booking.getStatus(), dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getEnd(), dtos.get(0).getEnd());
        assertEquals(booking.getItem().getName(), dtos.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), dtos.get(0).getBooker().getName());
    }
}
