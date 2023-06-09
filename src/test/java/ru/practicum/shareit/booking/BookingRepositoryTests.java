package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findBookingsOfUserInStateALL_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateALL(booker.getId(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStateALL_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateALL(booker.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateALL_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateALL(booker.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateREJECTED_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.CANCELED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateREJECTED(booker.getId(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStateREJECTED_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.CANCELED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateREJECTED(booker.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateREJECTED_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.CANCELED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateREJECTED(booker.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStatePAST_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.CANCELED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStatePAST(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStatePAST_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStatePAST(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStatePAST_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStatePAST(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateFUTURE_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateFUTURE(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStateFUTURE_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateFUTURE(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateFUTURE_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateFUTURE(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateCURRENT_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateCURRENT(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStateCURRENT_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateCURRENT(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateCURRENT_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateCURRENT(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateWAITING_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateWAITING(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfUserInStateWAITING_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateWAITING(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfUserInStateWAITING_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfUserInStateWAITING(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateALL_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateALL(booker.getId(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStateALL_firstPage_orderByStart() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateALL(owner.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateALL_secondPage_orderByStart() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateALL(owner.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateCURRENT_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateCURRENT(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStateCURRENT_firstPage_orderByID() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateCURRENT(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateCURRENT_secondPage_orderByID() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateCURRENT(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStatePAST_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStatePAST(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStatePAST_firstPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStatePAST(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStatePAST_secondPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStatePAST(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateFUTURE_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateFUTURE(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStateFUTURE_firstPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateFUTURE(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateFUTURE_secondPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateFUTURE(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateWAITING_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateWAITING(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStateWAITING_firstPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateWAITING(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateWAITING_secondPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateWAITING(owner.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateREJECTED_emptyWhenNothingFounded() {
        Pageable pageable = PageRequest.of(0, 5);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateREJECTED(booker.getId(), pageable);
        assertEquals(0, bookings.size());
    }

    @Test
    void findBookingsOfItemOwnerInStateREJECTED_firstPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.CANCELED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateREJECTED(owner.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking.getId(), bookings.get(0).getId());
        assertEquals(secondBooking.getStart(), bookings.get(0).getStart());
        assertEquals(secondBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(secondBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(secondBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findBookingsOfItemOwnerInStateREJECTED_secondPage_orderByStartDesc() {
        Pageable pageable = PageRequest.of(1, 1);
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.CANCELED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build());

        List<Booking> bookings = bookingRepository.findBookingsOfItemOwnerInStateREJECTED(owner.getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(firstBooking.getId(), bookings.get(0).getId());
        assertEquals(firstBooking.getStart(), bookings.get(0).getStart());
        assertEquals(firstBooking.getEnd(), bookings.get(0).getEnd());
        assertEquals(firstBooking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(firstBooking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findPreviousItemBooking_emptyWhenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.REJECTED)
                .build());

        Optional<Booking> book = bookingRepository.findPreviousItemBooking(item.getId(), LocalDateTime.now());
        assertTrue(book.isEmpty());
    }

    @Test
    void findPreviousItemBooking_foundLast_whenNotOneBookingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> book = bookingRepository.findPreviousItemBooking(item.getId(), LocalDateTime.now());
        assertFalse(book.isEmpty());
        assertEquals(secondBooking.getId(), book.get().getId());
        assertEquals(secondBooking.getBooker().getName(), book.get().getBooker().getName());
        assertEquals(secondBooking.getItem().getName(), book.get().getItem().getName());
        assertEquals(secondBooking.getStart(), book.get().getStart());
        assertEquals(secondBooking.getEnd(), book.get().getEnd());
    }

    @Test
    void findNextItemBooking_emptyWhenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.REJECTED)
                .build());

        Optional<Booking> book = bookingRepository.findNextItemBooking(item.getId(), LocalDateTime.now());
        assertTrue(book.isEmpty());
    }

    @Test
    void findNextItemBooking_foundLast_whenNotOneBookingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> book = bookingRepository.findNextItemBooking(item.getId(), LocalDateTime.now());
        assertFalse(book.isEmpty());
        assertEquals(firstBooking.getId(), book.get().getId());
        assertEquals(firstBooking.getBooker().getName(), book.get().getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), book.get().getItem().getName());
        assertEquals(firstBooking.getStart(), book.get().getStart());
        assertEquals(firstBooking.getEnd(), book.get().getEnd());
    }

    @Test
    void findOneApprovedBookingOfUser_emptyWhenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.REJECTED)
                .build());

        Optional<Booking> book = bookingRepository.findOneApprovedBookingOfUser(owner.getId());
        assertTrue(book.isEmpty());
    }

    @Test
    void findOneApprovedBookingOfUser_whenNotOneBookingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking firstBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());
        Booking secondBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> book = bookingRepository.findOneApprovedBookingOfUser(booker.getId());
        assertFalse(book.isEmpty());
        assertEquals(firstBooking.getId(), book.get().getId());
        assertEquals(firstBooking.getBooker().getName(), book.get().getBooker().getName());
        assertEquals(firstBooking.getItem().getName(), book.get().getItem().getName());
        assertEquals(firstBooking.getStart(), book.get().getStart());
        assertEquals(firstBooking.getEnd(), book.get().getEnd());
    }

    @Test
    void findOneApprovedBookingOfItemInPast_emptyWhenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> book = bookingRepository.findOneApprovedBookingOfItemInPast(item.getId(), LocalDateTime.now());
        assertTrue(book.isEmpty());
    }

    @Test
    void findOneApprovedBookingOfItemInPast_whenFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User booker = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item item = itemRepository.save(Item.builder().owner(owner).name("dollar").description("one dollar").available(true).build());
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(6))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> book = bookingRepository.findOneApprovedBookingOfItemInPast(item.getId(), LocalDateTime.now());
        assertFalse(book.isEmpty());
        assertEquals(booking.getId(), book.get().getId());
        assertEquals(booking.getBooker().getName(), book.get().getBooker().getName());
        assertEquals(booking.getItem().getName(), book.get().getItem().getName());
        assertEquals(booking.getStart(), book.get().getStart());
        assertEquals(booking.getEnd(), book.get().getEnd());
    }
}
