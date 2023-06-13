package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private ItemRequestService service;

    @Autowired
    public ItemRequestControllerTests(ObjectMapper mapper, MockMvc mvc) {
        this.objectMapper = mapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void create_correctAddingRequest() {
        User user = User.builder().id(5).name("Jo").email("jo@jo.jo").build();
        IncomeItemRequestDTO incomeDTO = IncomeItemRequestDTO.builder().description("I need a doctor!").build();
        OutcomeItemRequestDTO outcomeDTO = OutcomeItemRequestDTO.builder()
                .id(1)
                .description(incomeDTO.getDescription())
                .created(LocalDateTime.now())
                .build();

        when(service.addRequest(user.getId(), incomeDTO)).thenReturn(outcomeDTO);
        String response = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).addRequest(user.getId(), incomeDTO);
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void getOwnRequests_correctListWithTwoRequests() {
        int userID = 8;
        List<OutcomeItemRequestDTO> dtos = List.of(
                OutcomeItemRequestDTO.builder().id(1).created(LocalDateTime.now()).description("I need dollar").build(),
                OutcomeItemRequestDTO.builder().id(2).created(LocalDateTime.now()).description("I need phone").build()
        );

        doReturn(dtos).when(service).getRequestsOfUserByID(userID);
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getRequestsOfUserByID(userID);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void getOwnRequests_emptyList_whenNothingFounded() {
        int userID = 15;
        List<OutcomeItemRequestDTO> dtos = Collections.emptyList();

        doReturn(dtos).when(service).getRequestsOfUserByID(userID);
        String response = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getRequestsOfUserByID(userID);
        assertEquals("[]", response);
    }

    @SneakyThrows
    @Test
    void findRequestByID_gettingCorrectRequest() {
        User user = User.builder().id(2).name("Jo").email("jo@jo.jo").build();
        IncomeItemRequestDTO incomeDTO = IncomeItemRequestDTO.builder().description("I need a pill!").build();
        OutcomeItemRequestWithItemsDTO outcomeDTO = OutcomeItemRequestWithItemsDTO.builder()
                .id(1)
                .description(incomeDTO.getDescription())
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(service.getRequestByID(user.getId(), outcomeDTO.getId())).thenReturn(outcomeDTO);
        String response = mvc.perform(get("/requests/{requestId}", outcomeDTO.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getRequestByID(user.getId(), outcomeDTO.getId());
        assertEquals(response, objectMapper.writeValueAsString(outcomeDTO));
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_emptyList_whenParamsNotPresent() {
        int userID = 15;

        doReturn(Collections.emptyList()).when(service).getPageOfOtherUsersRequests(userID, 0, 5);
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getPageOfOtherUsersRequests(userID, 0, 5);
        assertEquals(response, "[]");
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_correctList_whenParamsNotPresent() {
        int userID = 98;
        List<OutcomeItemRequestWithItemsDTO> dtos = List.of(
                OutcomeItemRequestWithItemsDTO.builder().id(1)
                        .created(LocalDateTime.now())
                        .description("I need dollar")
                        .items(Collections.emptyList())
                        .build(),
                OutcomeItemRequestWithItemsDTO.builder().id(2)
                        .created(LocalDateTime.now())
                        .description("I need phone")
                        .items(Collections.emptyList())
                        .build()
        );

        doReturn(dtos).when(service).getPageOfOtherUsersRequests(userID, 0, 5);
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getPageOfOtherUsersRequests(userID, 0, 5);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }

    @SneakyThrows
    @Test
    void getPageOfItemRequests_correctList_whenCorrectParams() {
        int userID = 14;
        int from = 1;
        int size = 5;
        List<OutcomeItemRequestWithItemsDTO> dtos = List.of(
                OutcomeItemRequestWithItemsDTO.builder().id(1)
                        .created(LocalDateTime.now())
                        .description("I need dollar")
                        .items(Collections.emptyList())
                        .build(),
                OutcomeItemRequestWithItemsDTO.builder().id(2)
                        .created(LocalDateTime.now())
                        .description("I need phone")
                        .items(Collections.emptyList())
                        .build()
        );

        doReturn(dtos).when(service).getPageOfOtherUsersRequests(userID, from, size);
        String response = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(service, atMostOnce()).getPageOfOtherUsersRequests(userID, from, size);
        assertEquals(response, objectMapper.writeValueAsString(dtos));
    }
}
