package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private ItemClient client;

    @Autowired
    public ItemControllerTests(ObjectMapper mapper, MockMvc mvc) {
        this.objectMapper = mapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void create_badRequest_whenEmptyName() {
        int userID = 1;
        ItemDTO dto = ItemDTO.builder().description("one dollar").available(true).build();
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addItem(userID, dto);
    }

    @SneakyThrows
    @Test
    void create_badRequest_whenAvailableIsNull() {
        int userID = 1;
        ItemDTO dto = ItemDTO.builder().name("dollar").description("one dollar").available(null).build();
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addItem(userID, dto);
    }

    @SneakyThrows
    @Test
    void addComment_exception_whenTextEmpty() {
        int userID = 23;
        int itemID = 3;
        CommentDTO dto = CommentDTO.builder().build();

        mvc.perform(post("/items/{id}/comment", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(client, never()).addCommentToItemByUser(itemID, userID, dto);
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_exception_whenFromIncorrect() {
        int from = -2;
        int size = 5;
        int userID = 23;

        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getItemsOfUserByID(userID, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_exception_whenSizeIncorrect() {
        int from = 2;
        int size = 0;
        int userID = 23;

        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).getItemsOfUserByID(userID, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void findItemsByText_emptyList_whenTextIsBlank() {
        int userID = 23;
        int from = 0;
        int size = 5;
        String text = "  ";

        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userID)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).searchItemsByText(userID, text, from, size);
        assertEquals("[]", response);
    }

    @SneakyThrows
    @Test
    void findItemsByText_exception_whenFromIncorrect() {
        int userID = 23;
        int from = -7;
        int size = 5;
        String text = "dollar";

        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userID)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).searchItemsByText(userID, text, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }

    @SneakyThrows
    @Test
    void findItemsByText_exception_whenSizeIncorrect() {
        int userID = 23;
        int from = 0;
        int size = 0;
        String text = "dollar";

        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userID)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(client, never()).searchItemsByText(userID, text, from, size);
        assertEquals("{\"error\":\"Page or size can't be negative\"}", response);
    }
}
