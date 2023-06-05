package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestsServiceTests {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl service;

    @Test
    void addRequest_exception_whenUserNotPresented() {
        int userID = 5;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        IncomeItemRequestDTO incomeDTO = IncomeItemRequestDTO.builder().description("I need dollar").build();

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.addRequest(userID, incomeDTO));

        verify(requestRepository, never()).save(RequestMapper.incomeDtoToItemRequest(incomeDTO, user));
        assertEquals("User with ID 5 not present", exception.getMessage());
    }

    @Test
    void addRequest_correctAdding() {
        int userID = 8;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        IncomeItemRequestDTO incomeDTO = IncomeItemRequestDTO.builder().description("I need dollar").build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));

        OutcomeItemRequestDTO outcomeDTO = service.addRequest(userID, incomeDTO);
        assertEquals(outcomeDTO.getDescription(), incomeDTO.getDescription());
    }

    @Test
    void getRequestsOfUserByID_exception_whenIncorrectUserID() {
        int userID = 11;

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getRequestsOfUserByID(userID));

        verify(requestRepository, never()).findAllByUserID(userID);
        assertEquals("User with ID 11 not present", exception.getMessage());
    }

    @Test
    void getRequestsOfUserByID_correctListWithoutItems() {
        int userID = 14;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        List<ItemRequest> requests = List.of(
                ItemRequest.builder().id(1).requestor(user).description("I need dollar").created(LocalDateTime.now()).build()
        );

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.findAllByUserID(userID)).thenReturn(requests);
        when(itemRepository.findAllByRequestID(requests.get(0).getId())).thenReturn(Collections.emptyList());
        List<OutcomeItemRequestWithItemsDTO> dtos = service.getRequestsOfUserByID(userID);

        verify(requestRepository, atMostOnce()).findAllByUserID(userID);
        assertEquals(1, dtos.size());
        assertEquals("I need dollar", dtos.get(0).getDescription());
        assertEquals(0, dtos.get(0).getItems().size());
        assertTrue(LocalDateTime.now().isAfter(dtos.get(0).getCreated()));
    }

    @Test
    void getRequestsOfUserByID_correctListWithItems() {
        int userID = 1;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        List<Item> items = List.of(
                Item.builder().id(1).owner(user).name("dollar").description("one dollar").available(true).build()
        );
        List<ItemRequest> requests = List.of(
                ItemRequest.builder().id(1).requestor(user).description("I need dollar").created(LocalDateTime.now()).build()
        );

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.findAllByUserID(userID)).thenReturn(requests);
        when(itemRepository.findAllByRequestID(requests.get(0).getId())).thenReturn(items);
        List<OutcomeItemRequestWithItemsDTO> dtos = service.getRequestsOfUserByID(userID);

        verify(requestRepository, atMostOnce()).findAllByUserID(userID);
        assertEquals(1, dtos.size());
        assertEquals("I need dollar", dtos.get(0).getDescription());
        assertEquals(1, dtos.get(0).getItems().size());
        assertEquals(items.get(0).getName(), dtos.get(0).getItems().get(0).getName());
        assertTrue(LocalDateTime.now().isAfter(dtos.get(0).getCreated()));
    }

    @Test
    void getRequestByID_exception_whenIncorrectUserID() {
        int userID = 25;
        int requestID = 4;

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getRequestByID(userID, requestID));

        verify(requestRepository, never()).findById(requestID);
        assertEquals("User with ID 25 not present", exception.getMessage());
    }

    @Test
    void getRequestByID_exception_whenIncorrectRequestID() {
        int userID = 32;
        int requestID = 54;

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.existsById(requestID)).thenReturn(false);
        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> service.getRequestByID(userID, requestID));

        verify(requestRepository, never()).findById(requestID);
        assertEquals("Item request with ID 54 not presented", exception.getMessage());
    }

    @Test
    void getRequestByID_correctListWithoutItems() {
        int userID = 23;
        int requestID = 11;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder()
                .id(requestID)
                .requestor(user)
                .description("I need dollar")
                .created(LocalDateTime.now())
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.existsById(requestID)).thenReturn(true);
        when(requestRepository.findById(requestID)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestID(request.getId())).thenReturn(Collections.emptyList());
        OutcomeItemRequestWithItemsDTO dto = service.getRequestByID(userID, requestID);

        verify(requestRepository, atMostOnce()).findById(requestID);
        assertEquals("I need dollar", dto.getDescription());
        assertEquals(0, dto.getItems().size());
        assertTrue(LocalDateTime.now().isAfter(dto.getCreated()));
    }

    @Test
    void getRequestByID_correctListWithItems() {
        int userID = 11;
        int requestID = 17;
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder()
                .id(requestID)
                .requestor(user)
                .description("I need dollar")
                .created(LocalDateTime.now())
                .build();
        List<Item> items = List.of(
                Item.builder().id(1).owner(user).name("dollar").description("one dollar").available(true).build()
        );

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.existsById(requestID)).thenReturn(true);
        when(requestRepository.findById(requestID)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestID(request.getId())).thenReturn(items);
        OutcomeItemRequestWithItemsDTO dto = service.getRequestByID(userID, requestID);

        verify(requestRepository, atMostOnce()).findById(requestID);
        assertEquals("I need dollar", dto.getDescription());
        assertEquals(1, dto.getItems().size());
        assertEquals(items.get(0).getName(), dto.getItems().get(0).getName());
        assertTrue(LocalDateTime.now().isAfter(dto.getCreated()));
    }

    @Test
    void getPageOfOtherUsersRequests_exception_whenIncorrectUserID() {
        int userID = 45;
        int from = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getPageOfOtherUsersRequests(userID, from, size));

        verify(requestRepository, never()).findAllFromAnotherUsers(userID, pageable);
        assertEquals("User with ID 45 not present", exception.getMessage());
    }

    @Test
    void getPageOfOtherUsersRequests_emptyList_whenNothingFounded() {
        int userID = 12;
        int from = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.findAllFromAnotherUsers(userID, pageable)).thenReturn(Collections.emptyList());
        List<OutcomeItemRequestWithItemsDTO> dtos = service.getPageOfOtherUsersRequests(userID, from, size);

        verify(requestRepository, atMostOnce()).findAllFromAnotherUsers(userID, pageable);
        assertEquals(0, dtos.size());
    }

    @Test
    void getPageOfOtherUsersRequests_correctListWithoutItems() {
        int userID = 86;
        int requestID = 34;
        int from = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        List<ItemRequest> requests = List.of(ItemRequest.builder()
                .id(requestID)
                .requestor(user)
                .description("I need dollar")
                .created(LocalDateTime.now())
                .build()
        );

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.findAllFromAnotherUsers(userID, pageable)).thenReturn(requests);
        when(itemRepository.findAllByRequestID(requests.get(0).getId())).thenReturn(Collections.emptyList());
        List<OutcomeItemRequestWithItemsDTO> dtos = service.getPageOfOtherUsersRequests(userID, from, size);

        verify(requestRepository, atMostOnce()).findAllFromAnotherUsers(requestID, pageable);
        assertEquals("I need dollar", dtos.get(0).getDescription());
        assertEquals(0, dtos.get(0).getItems().size());
        assertTrue(LocalDateTime.now().isAfter(dtos.get(0).getCreated()));
    }

    @Test
    void getPageOfOtherUsersRequests_correctListWithItems() {
        int userID = 56;
        int requestID = 18;
        int from = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        User user = User.builder().id(userID).name("Jo").email("j@i.jo").build();
        List<ItemRequest> requests = List.of(ItemRequest.builder()
                .id(requestID)
                .requestor(user)
                .description("I need dollar")
                .created(LocalDateTime.now())
                .build()
        );
        List<Item> items = List.of(
                Item.builder().id(1).owner(user).name("dollar").description("one dollar").available(true).build()
        );

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.findAllFromAnotherUsers(userID, pageable)).thenReturn(requests);
        when(itemRepository.findAllByRequestID(requests.get(0).getId())).thenReturn(items);
        List<OutcomeItemRequestWithItemsDTO> dtos = service.getPageOfOtherUsersRequests(userID, from, size);

        verify(requestRepository, atMostOnce()).findAllFromAnotherUsers(requestID, pageable);
        assertEquals("I need dollar", dtos.get(0).getDescription());
        assertEquals(1, dtos.get(0).getItems().size());
        assertEquals("dollar", dtos.get(0).getItems().get(0).getName());
        assertTrue(LocalDateTime.now().isAfter(dtos.get(0).getCreated()));
    }
}
