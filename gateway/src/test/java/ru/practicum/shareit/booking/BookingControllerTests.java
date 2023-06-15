package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingStateRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private BookingClient client;

    @Autowired
    public BookingControllerTests(ObjectMapper mapper, MockMvc mvc) {
        this.objectMapper = mapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_error_whenSizeIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 0;

        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", BookingStateRequest.ALL.name()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getBookingsOfUserItemsByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_error_whenSizeIncorrect() {
        int userID = 88;
        int from = 0;
        int size = 0;

        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", BookingStateRequest.ALL.name()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getBookingsOfUserByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
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

        verify(client, never()).getBookingsOfUserByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals(response, "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}");
    }

    @SneakyThrows
    @Test
    void findBookingsOfUserInState_error_whenFromIncorrect() {
        int userID = 88;
        int from = -5;
        int size = 5;

        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", BookingStateRequest.ALL.name()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getBookingsOfUserByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void create_error_whenEndIsNull() {
        int userID = 2;
        BookingDTO incomeDTO = BookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .itemId(6)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void create_error_whenItemIdIsNull() {
        int userID = 4;
        BookingDTO incomeDTO = BookingDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void create_error_whenStartInPast() {
        int userID = 9;
        BookingDTO incomeDTO = BookingDTO.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(6)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addBooking(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void findBookingsOfItemOwnerByState_error_whenFromIncorrect() {
        int userID = 88;
        int from = -5;
        int size = 5;

        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", BookingStateRequest.ALL.name()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getBookingsOfUserItemsByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
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

        verify(client, never()).getBookingsOfUserItemsByState(userID, BookingStateRequest.ALL, from, size);
        assertEquals(response, "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}");
    }
}
