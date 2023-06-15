package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private ItemRequestClient client;

    @Autowired
    public ItemRequestControllerTests(ObjectMapper mapper, MockMvc mvc) {
        this.objectMapper = mapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_exception_whenSizeIncorrect() {
        int userID = 11;
        int from = 5;
        int size = -4;

        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getPageOfOtherUsersRequests(userID, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void create_whenEmptyDescription() {
        int userID = 5;
        ItemRequestDTO incomeDTO = ItemRequestDTO.builder().build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addRequest(userID, incomeDTO);
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_exception_whenFromIncorrect() {
        int userID = 34;
        int from = -5;
        int size = 4;

        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getPageOfOtherUsersRequests(userID, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_exception_whenSizeAndFromIncorrect() {
        int userID = 24;
        int from = -5;
        int size = 0;

        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getPageOfOtherUsersRequests(userID, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

}
