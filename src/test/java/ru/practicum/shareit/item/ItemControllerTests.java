package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private ItemService service;

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

        verify(service, never()).addItem(userID, dto);
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

        verify(service, never()).addItem(userID, dto);
    }

    @SneakyThrows
    @Test
    void create_correctCreation_withoutRequestID() {
        int userID = 1;
        ItemDTO dto = ItemDTO.builder().name("dollar").description("one dollar").available(true).build();
        ItemDTO outcomeDTO = ItemDTO.builder().id(1)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .requestId(dto.getRequestId())
                .build();

        when(service.addItem(userID, dto)).thenReturn(outcomeDTO);
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).addItem(userID, dto);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void create_correctCreation_withRequestID() {
        int userID = 5;
        ItemDTO dto = ItemDTO.builder().name("dollar").description("one dollar").available(true).requestId(6).build();
        ItemDTO outcomeDTO = ItemDTO.builder().id(1)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .requestId(dto.getRequestId())
                .build();

        when(service.addItem(userID, dto)).thenReturn(outcomeDTO);
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).addItem(userID, dto);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void addComment_exception_whenTextEmpty() {
        int userID = 23;
        int itemID = 3;
        IncomeCommentDTO dto = IncomeCommentDTO.builder().build();

        mvc.perform(post("/items/{id}/comment", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(service, never()).addCommentToItemByUser(itemID, userID, dto);
    }

    @SneakyThrows
    @Test
    void addComment_correctAddComment() {
        int userID = 52;
        int itemID = 11;
        IncomeCommentDTO dto = IncomeCommentDTO.builder().text("Cool dollar").created(LocalDateTime.now()).build();
        OutcomeCommentDTO outcomeDTO = OutcomeCommentDTO.builder().id(1).authorName("Jo").created(dto.getCreated()).build();

        when(service.addCommentToItemByUser(itemID, userID, dto)).thenReturn(outcomeDTO);
        String response = mvc.perform(post("/items/{id}/comment", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).addCommentToItemByUser(itemID, userID, dto);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_exception_whenFromIncorrect() {
        int from = -2;
        int size = 5;
        int userID = 23;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getItemsOfUserByID(userID, from, size);
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_exception_whenSizeIncorrect() {
        int from = 2;
        int size = 0;
        int userID = 23;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, never()).getItemsOfUserByID(userID, from, size);
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_correctGetting_WithoutBookingAndComments() {
        int from = 0;
        int size = 5;
        int userID = 23;
        List<ItemWithBookingsAndCommentsDTO> dtos = List.of(
                ItemWithBookingsAndCommentsDTO.builder().id(5)
                        .name("Dollar")
                        .description("One dollar")
                        .available(true)
                        .comments(Collections.emptyList())
                        .lastBooking(null)
                        .nextBooking(null)
                        .build()
        );

        doReturn(dtos).when(service).getItemsOfUserByID(userID, from, size);
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemsOfUserByID(userID, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_correctGetting_WithBooking() {
        int from = 0;
        int size = 5;
        int userID = 23;
        List<ItemWithBookingsAndCommentsDTO> dtos = List.of(
                ItemWithBookingsAndCommentsDTO.builder().id(5)
                        .name("Dollar")
                        .description("One dollar")
                        .available(true)
                        .comments(Collections.emptyList())
                        .lastBooking(ShortBooking.builder().id(3).bookerId(6).build())
                        .nextBooking(ShortBooking.builder().id(7).bookerId(18).build())
                        .build()
        );

        doReturn(dtos).when(service).getItemsOfUserByID(userID, from, size);
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemsOfUserByID(userID, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_correctGetting_WithBookingAndComments() {
        int from = 0;
        int size = 5;
        int userID = 23;
        List<ItemWithBookingsAndCommentsDTO> dtos = List.of(
                ItemWithBookingsAndCommentsDTO.builder().id(5)
                        .name("Dollar")
                        .description("One dollar")
                        .available(true)
                        .comments(List.of(OutcomeCommentDTO.builder().id(3)
                                .text("cool dollar")
                                .authorName("Jo")
                                .created(LocalDateTime.now().minusDays(3))
                                .build()))
                        .lastBooking(ShortBooking.builder().id(3).bookerId(6).build())
                        .nextBooking(ShortBooking.builder().id(7).bookerId(18).build())
                        .build()
        );

        doReturn(dtos).when(service).getItemsOfUserByID(userID, from, size);
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemsOfUserByID(userID, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void patch_correctPatching_whenSomeFieldsIsNull() {
        int userID = 45;
        int itemID = 22;
        ItemDTO incomeDTO = ItemDTO.builder().id(itemID).description("five dollars").build();
        ItemDTO outcomeDTO = ItemDTO.builder().id(itemID).name("dollar").description(incomeDTO.getDescription()).available(true).build();

        doReturn(outcomeDTO).when(service).patchItem(userID, incomeDTO);
        String response = mvc.perform(patch("/items/{id}", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).patchItem(userID, incomeDTO);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void findItemByID_correctGetting_WithoutBookingAndComments() {
        int userID = 23;
        int itemID = 5;
        ItemWithBookingsAndCommentsDTO dto = ItemWithBookingsAndCommentsDTO.builder()
                .id(itemID)
                .name("Dollar")
                .description("One dollar")
                .available(true)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .nextBooking(null)
                .build();

        when(service.getItemByID(itemID, userID)).thenReturn(dto);
        String response = mvc.perform(get("/items/{id}", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemByID(itemID, userID);
        assertEquals(response, objectMapper.writeValueAsString(dto));
    }

    @SneakyThrows
    @Test
    void findItemByID_correctGetting_WithBooking() {
        int userID = 23;
        int itemID = 5;
        ItemWithBookingsAndCommentsDTO dto = ItemWithBookingsAndCommentsDTO.builder()
                .id(itemID)
                .name("Dollar")
                .description("One dollar")
                .available(true)
                .comments(Collections.emptyList())
                .lastBooking(ShortBooking.builder().id(6).bookerId(58).build())
                .nextBooking(ShortBooking.builder().id(85).bookerId(782).build())
                .build();

        when(service.getItemByID(itemID, userID)).thenReturn(dto);
        String response = mvc.perform(get("/items/{id}", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemByID(itemID, userID);
        assertEquals(response, objectMapper.writeValueAsString(dto));
    }

    @SneakyThrows
    @Test
    void findItemByID_correctGetting_WithBookingAndComments() {
        int userID = 23;
        int itemID = 5;
        ItemWithBookingsAndCommentsDTO dto = ItemWithBookingsAndCommentsDTO.builder()
                .id(itemID)
                .name("Dollar")
                .description("One dollar")
                .available(true)
                .comments(List.of(OutcomeCommentDTO.builder().id(74)
                        .text("Cool dollar!")
                        .authorName("Jo")
                        .created(LocalDateTime.now().minusDays(5))
                        .build()))
                .lastBooking(ShortBooking.builder().id(6).bookerId(58).build())
                .nextBooking(ShortBooking.builder().id(85).bookerId(782).build())
                .build();

        when(service.getItemByID(itemID, userID)).thenReturn(dto);
        String response = mvc.perform(get("/items/{id}", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getItemByID(itemID, userID);
        assertEquals(response, objectMapper.writeValueAsString(dto));
    }

    @SneakyThrows
    @Test
    void findItemsByText_emptyList_whenTextIsBlank() {
        int from = 0;
        int size = 5;
        String text = "  ";

        String response = mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, never()).searchItemsByText(text, from, size);
        assertEquals("[]", response);
    }

    @SneakyThrows
    @Test
    void findItemsByText_exception_whenFromIncorrect() {
        int from = -7;
        int size = 5;
        String text = "dollar";

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, never()).searchItemsByText(text, from, size);
    }

    @SneakyThrows
    @Test
    void findItemsByText_exception_whenSizeIncorrect() {
        int from = 0;
        int size = 0;
        String text = "dollar";

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, never()).searchItemsByText(text, from, size);
    }

    @SneakyThrows
    @Test
    void findItemsByText_emptyList_whenNothingFounded() {
        int from = 0;
        int size = 5;
        String text = "dollar";

        when(service.searchItemsByText(text, from, size)).thenReturn(Collections.emptyList());
        String response = mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).searchItemsByText(text, from, size);
        assertEquals(response, "[]");
    }

    @SneakyThrows
    @Test
    void findItemsByText_correctGetting() {
        int from = 0;
        int size = 5;
        String text = "dollar";
        List<ItemDTO> dtos = List.of(ItemDTO.builder().id(8)
                .name("dollar")
                .description("one dollar")
                .available(true)
                .build());

        when(service.searchItemsByText(text, from, size)).thenReturn(dtos);
        String response = mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).searchItemsByText(text, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }
}
