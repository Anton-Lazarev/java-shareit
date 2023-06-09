package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    BookingService service;

    @SneakyThrows
    @Test
    void create_error_whenItemIdIsNull() {
        int userID = 4;
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void create_error_whenStartInPast() {
        int userID = 9;
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(6)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void create_error_whenEndIsNull() {
        int userID = 2;
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .itemId(6)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(service, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void create_correctCreation() {
        int userID = 2;
        int itemID = 9;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).owner(user).name("dollar").description("one dollar").available(true).build();
        IncomeBookingDTO incomeDTO = IncomeBookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(itemID)
                .build();
        OutcomeBookingDTO outcomeDTO = OutcomeBookingDTO.builder()
                .id(1)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(incomeDTO.getStart())
                .end(incomeDTO.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        when(service.addBooking(userID, incomeDTO)).thenReturn(outcomeDTO);
        String response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).addBooking(userID, incomeDTO);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void patch_error_whenParamNotPresent() {
        int userID = 88;
        int bookingID = 70;

        mvc.perform(patch("/bookings/{bookingID}", bookingID)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isBadRequest());

        verify(service, never()).changeBookingStatus(userID, bookingID, true);
    }

    @SneakyThrows
    @Test
    void patch_correctApproveBooking() {
        int userID = 88;
        int bookingID = 70;
        boolean approve = true;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(58).owner(user).name("dollar").description("one dollar").available(true).build();
        OutcomeBookingDTO outcomeDTO = OutcomeBookingDTO.builder()
                .id(bookingID)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        when(service.changeBookingStatus(userID, bookingID, approve)).thenReturn(outcomeDTO);
        String response = mvc.perform(patch("/bookings/{bookingID}", bookingID)
                        .header("X-Sharer-User-Id", userID)
                        .param("approved", String.valueOf(approve)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).changeBookingStatus(userID, bookingID, approve);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void patch_correctRejectBooking() {
        int userID = 11;
        int bookingID = 21;
        boolean approve = false;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(36).owner(user).name("dollar").description("one dollar").available(true).build();
        OutcomeBookingDTO outcomeDTO = OutcomeBookingDTO.builder()
                .id(bookingID)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        when(service.changeBookingStatus(userID, bookingID, approve)).thenReturn(outcomeDTO);
        String response = mvc.perform(patch("/bookings/{bookingID}", bookingID)
                        .header("X-Sharer-User-Id", userID)
                        .param("approved", String.valueOf(approve)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).changeBookingStatus(userID, bookingID, approve);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void findBookingByID_correctGetting() {
        int userID = 1151;
        int bookingID = 724;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(85).owner(user).name("dollar").description("one dollar").available(true).build();
        OutcomeBookingDTO outcomeDTO = OutcomeBookingDTO.builder()
                .id(bookingID)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        when(service.getBookingByID(userID, bookingID)).thenReturn(outcomeDTO);
        String response = mvc.perform(get("/bookings/{bookingID}", bookingID)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getBookingByID(userID, bookingID);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_error_whenStateIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 5;
        String state = "UNIC";

        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(service, never()).getBookingsOfUserByState(userID, state, from, size);
        assertEquals(response, "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}");
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_error_whenFromIncorrect() {
        int userID = 88;
        int from = -5;
        int size = 5;
        String state = "ALL";

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getBookingsOfUserByState(userID, state, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_error_whenSizeIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 0;
        String state = "ALL";

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getBookingsOfUserByState(userID, state, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_correctGetting_whenWithoutParams() {
        int userID = 64;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(74).owner(user).name("dollar").description("one dollar").available(true).build();
        List<OutcomeBookingDTO> dtos = List.of(OutcomeBookingDTO.builder()
                .id(15)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        when(service.getBookingsOfUserByState(userID, "ALL", 0, 5)).thenReturn(dtos);
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getBookingsOfUserByState(userID, "ALL", 0, 5);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_correctGetting_withParams() {
        int userID = 64;
        int from = 1;
        int size = 3;
        String state = "WAITING";
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(74).owner(user).name("dollar").description("one dollar").available(true).build();
        List<OutcomeBookingDTO> dtos = List.of(OutcomeBookingDTO.builder()
                .id(15)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        when(service.getBookingsOfUserByState(userID, state, from, size)).thenReturn(dtos);
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getBookingsOfUserByState(userID, state, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_error_whenStateIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 5;
        String state = "UNIC";

        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(service, never()).getBookingsOfUserItemsByState(userID, state, from, size);
        assertEquals(response, "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}");
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_error_whenFromIncorrect() {
        int userID = 88;
        int from = -5;
        int size = 5;
        String state = "ALL";

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getBookingsOfUserItemsByState(userID, state, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_error_whenSizeIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 0;
        String state = "ALL";

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getBookingsOfUserItemsByState(userID, state, from, size);
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_correctGetting_whenWithoutParams() {
        int userID = 64;
        User user = User.builder().id(63).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(74).owner(user).name("dollar").description("one dollar").available(true).build();
        List<OutcomeBookingDTO> dtos = List.of(OutcomeBookingDTO.builder()
                .id(15)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        when(service.getBookingsOfUserItemsByState(userID, "ALL", 0, 5)).thenReturn(dtos);
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getBookingsOfUserItemsByState(userID, "ALL", 0, 5);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_correctGetting_withParams() {
        int userID = 64;
        int from = 1;
        int size = 3;
        String state = "WAITING";
        User user = User.builder().id(125).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(74).owner(user).name("dollar").description("one dollar").available(true).build();
        List<OutcomeBookingDTO> dtos = List.of(OutcomeBookingDTO.builder()
                .id(15)
                .booker(UserMapper.userToShortUser(user))
                .item(ItemMapper.itemToShortItem(item))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        when(service.getBookingsOfUserItemsByState(userID, state, from, size)).thenReturn(dtos);
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getBookingsOfUserItemsByState(userID, state, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }
}
