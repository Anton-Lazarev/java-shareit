package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.Paginator;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectItemOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotBookedItemException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl service;

    @Test
    void addItem_exception_whenUserNotPresent() {
        int userID = 2;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().name("dollar").description("one dollar").available(true).build();

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.addItem(userID, incomeDTO));

        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(incomeDTO, owner));
        assertEquals("User with ID 2 not present", exception.getMessage());
    }

    @Test
    void addItem_correctSave_withoutRequest() {
        int userID = 5;
        ItemDTO incomeDTO = ItemDTO.builder().name("dollar").description("one dollar").available(true).build();
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        Item savedItem = Item.builder().id(1)
                .name(incomeDTO.getName())
                .description(incomeDTO.getDescription())
                .available(incomeDTO.getAvailable())
                .owner(owner)
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(userRepository.findById(userID)).thenReturn(Optional.of(owner));
        ItemDTO outcomeDTO = service.addItem(userID, incomeDTO);

        assertEquals(savedItem.getName(), outcomeDTO.getName());
        assertEquals(savedItem.getDescription(), outcomeDTO.getDescription());
        assertEquals(savedItem.getAvailable(), outcomeDTO.getAvailable());
        assertEquals(0, outcomeDTO.getRequestId());
    }

    @Test
    void addItem_exception_whenRequestIdIncorrect() {
        int userID = 11;
        int requestID = 5;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().name("dollar").description("one dollar").available(true).requestId(requestID).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(requestRepository.existsById(requestID)).thenReturn(false);
        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> service.addItem(userID, incomeDTO));

        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(incomeDTO, owner));
        assertEquals("Item request with ID 5 not presented", exception.getMessage());
    }

    @Test
    void addItem_correctSave_withRequest() {
        int userID = 85;
        int requestID = 12;
        ItemDTO incomeDTO = ItemDTO.builder().name("dollar").description("one dollar").available(true).requestId(requestID).build();
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder()
                .id(requestID)
                .requestor(owner)
                .description("need money")
                .created(LocalDateTime.now().minusDays(2))
                .build();
        Item savedItem = Item.builder().id(1)
                .name(incomeDTO.getName())
                .description(incomeDTO.getDescription())
                .available(incomeDTO.getAvailable())
                .owner(owner)
                .request(request)
                .build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(userRepository.findById(userID)).thenReturn(Optional.of(owner));
        when(requestRepository.existsById(requestID)).thenReturn(true);
        when(requestRepository.findById(requestID)).thenReturn(Optional.of(request));
        ItemDTO outcomeDTO = service.addItem(userID, incomeDTO);

        assertEquals(savedItem.getName(), outcomeDTO.getName());
        assertEquals(savedItem.getDescription(), outcomeDTO.getDescription());
        assertEquals(savedItem.getAvailable(), outcomeDTO.getAvailable());
        assertEquals(savedItem.getRequest().getId(), outcomeDTO.getRequestId());
    }

    @Test
    void patchItem_exception_whenUserNotPresent() {
        int userID = 69;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().id(45).name("dollar").description("one dollar").available(true).build();

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.patchItem(userID, incomeDTO));

        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(incomeDTO, owner));
        assertEquals("User with ID 69 not present", exception.getMessage());
    }

    @Test
    void patchItem_exception_whenUserNotItemOwner() {
        int userID = 34;
        User owner = User.builder().id(14).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().id(45).name("dollar").description("one dollar").available(true).build();
        Item item = ItemMapper.itemDtoToItem(incomeDTO, owner);

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(incomeDTO.getId())).thenReturn(Optional.of(item));
        IncorrectItemOwnerException exception = assertThrows(IncorrectItemOwnerException.class,
                () -> service.patchItem(userID, incomeDTO));

        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(incomeDTO, owner));
        assertEquals("User with ID 34 not owner of current item with ID 45", exception.getMessage());
    }

    @Test
    void patchItem_patched_whenOneField() {
        int userID = 34;
        int itemID = 11;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().id(itemID).name("patch").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(incomeDTO.getId())).thenReturn(Optional.of(item));
        ItemDTO outcomeDTO = service.patchItem(userID, incomeDTO);

        assertEquals(item.getId(), outcomeDTO.getId());
        assertEquals(incomeDTO.getName(), outcomeDTO.getName());
        assertEquals(item.getDescription(), outcomeDTO.getDescription());
        assertEquals(item.getAvailable(), outcomeDTO.getAvailable());
    }

    @Test
    void patchItem_patched_whenTwoFields() {
        int userID = 54;
        int itemID = 18;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().id(itemID).name("patch").available(false).build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(incomeDTO.getId())).thenReturn(Optional.of(item));
        ItemDTO outcomeDTO = service.patchItem(userID, incomeDTO);

        assertEquals(item.getId(), outcomeDTO.getId());
        assertEquals(incomeDTO.getName(), outcomeDTO.getName());
        assertEquals(item.getDescription(), outcomeDTO.getDescription());
        assertEquals(incomeDTO.getAvailable(), outcomeDTO.getAvailable());
    }

    @Test
    void patchItem_patched_whenAllFields() {
        int userID = 32;
        int itemID = 12;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        ItemDTO incomeDTO = ItemDTO.builder().id(itemID).name("patch").description("patched").available(false).build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(incomeDTO.getId())).thenReturn(Optional.of(item));
        ItemDTO outcomeDTO = service.patchItem(userID, incomeDTO);

        assertEquals(item.getId(), outcomeDTO.getId());
        assertEquals(incomeDTO.getName(), outcomeDTO.getName());
        assertEquals(incomeDTO.getDescription(), outcomeDTO.getDescription());
        assertEquals(incomeDTO.getAvailable(), outcomeDTO.getAvailable());
    }

    @Test
    void getItemByID_exception_whenItemNotPresented() {
        int userID = 35;
        int itemID = 23;

        when(itemRepository.existsById(itemID)).thenReturn(false);
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> service.getItemByID(itemID, userID));

        verify(itemRepository, never()).findById(itemID);
        assertEquals("Item with ID 23 not present", exception.getMessage());
    }

    @Test
    void getItemByID_exception_whenUserNotPresented() {
        int userID = 35;
        int itemID = 23;

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getItemByID(itemID, userID));

        verify(itemRepository, never()).findById(itemID);
        assertEquals("User with ID 35 not present", exception.getMessage());
    }

    @Test
    void getItemByID_gettingWithEmptyComments_whenNotOwner() {
        int userID = 76;
        int itemID = 86;
        User owner = User.builder().id(15).name("jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemID(item.getId())).thenReturn(Collections.emptyList());
        ItemWithBookingsAndCommentsDTO dto = service.getItemByID(itemID, userID);

        verify(itemRepository, atMostOnce()).findById(itemID);
        verify(commentRepository, atMostOnce()).findAllByItemID(item.getId());
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(0, dto.getComments().size());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void getItemByID_gettingWithComments_whenNotOwner() {
        int userID = 12;
        int itemID = 57;
        User owner = User.builder().id(15).name("jo").email("j@i.jo").build();
        User author = User.builder().id(85).name("leo").email("l@e.o").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();
        List<Comment> comments = List.of(Comment.builder().id(43).author(author).item(item).text("love money").created(LocalDateTime.now().minusDays(1)).build());

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemID(item.getId())).thenReturn(comments);
        ItemWithBookingsAndCommentsDTO dto = service.getItemByID(itemID, userID);

        verify(itemRepository, atMostOnce()).findById(itemID);
        verify(commentRepository, atMostOnce()).findAllByItemID(item.getId());
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(1, dto.getComments().size());
        assertEquals(comments.get(0).getText(), dto.getComments().get(0).getText());
        assertEquals(comments.get(0).getCreated(), dto.getComments().get(0).getCreated());
        assertEquals(comments.get(0).getAuthor().getName(), dto.getComments().get(0).getAuthorName());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void getItemByID_gettingWithEmptyCommentsAndBookings_whenOwner() {
        int userID = 76;
        int itemID = 86;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemID(item.getId())).thenReturn(Collections.emptyList());
        ItemWithBookingsAndCommentsDTO dto = service.getItemByID(itemID, userID);

        verify(itemRepository, atMostOnce()).findById(itemID);
        verify(commentRepository, atMostOnce()).findAllByItemID(item.getId());
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(0, dto.getComments().size());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void getItemByID_gettingWithComments_whenOwner() {
        int userID = 12;
        int itemID = 57;
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        User author = User.builder().id(85).name("leo").email("l@e.o").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();
        List<Comment> comments = List.of(Comment.builder().id(43).author(author).item(item).text("love money").created(LocalDateTime.now().minusDays(1)).build());

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemID(item.getId())).thenReturn(comments);
        ItemWithBookingsAndCommentsDTO dto = service.getItemByID(itemID, userID);

        verify(itemRepository, atMostOnce()).findById(itemID);
        verify(commentRepository, atMostOnce()).findAllByItemID(item.getId());
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(1, dto.getComments().size());
        assertEquals(comments.get(0).getText(), dto.getComments().get(0).getText());
        assertEquals(comments.get(0).getCreated(), dto.getComments().get(0).getCreated());
        assertEquals(comments.get(0).getAuthor().getName(), dto.getComments().get(0).getAuthorName());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void getItemsOfUserByID_exception_whenUserNotPresented() {
        int userID = 56;
        int from = 0;
        int size = 5;

        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getItemsOfUserByID(userID, from, size));

        verify(itemRepository, never()).findAllByUserId(userID, PageRequest.of(from / size, size));
        assertEquals("User with ID 56 not present", exception.getMessage());
    }

    @Test
    void getItemsOfUserByID_gettingWithEmptyCommentsAndBookings_whenOwner() {
        int userID = 14;
        int from = 0;
        int size = 5;
        Paginator paginator = new Paginator(from, size);
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        List<Item> items = List.of(Item.builder().id(75).name("dollar").description("one dollar").owner(owner).available(true).build());

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findAllByUserId(userID, paginator)).thenReturn(items);
        List<ItemWithBookingsAndCommentsDTO> dtos = List.copyOf(service.getItemsOfUserByID(userID, from, size));

        verify(itemRepository, atMostOnce()).findAllByUserId(userID, paginator);
        assertEquals(1, dtos.size());
        assertEquals(items.get(0).getName(), dtos.get(0).getName());
        assertEquals(items.get(0).getDescription(), dtos.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), dtos.get(0).getAvailable());
        assertEquals(0, dtos.get(0).getComments().size());
        assertNull(dtos.get(0).getLastBooking());
        assertNull(dtos.get(0).getNextBooking());
    }

    @Test
    void getItemsOfUserByID_gettingWithComments_whenOwner() {
        int userID = 12;
        int itemID = 57;
        int from = 0;
        int size = 5;
        Paginator paginator = new Paginator(from, size);
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        User author = User.builder().id(85).name("leo").email("l@e.o").build();
        List<Item> items = List.of(Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build());
        List<Comment> comments = List.of(Comment.builder().id(43).author(author).item(items.get(0)).text("love money").created(LocalDateTime.now().minusDays(1)).build());

        when(userRepository.existsById(userID)).thenReturn(true);
        when(itemRepository.findAllByUserId(userID, paginator)).thenReturn(items);
        when(commentRepository.findAllByItemID(items.get(0).getId())).thenReturn(comments);
        List<ItemWithBookingsAndCommentsDTO> dtos = List.copyOf(service.getItemsOfUserByID(userID, from, size));

        verify(itemRepository, atMostOnce()).findAllByUserId(userID, paginator);
        verify(commentRepository, atMostOnce()).findAllByItemID(items.get(0).getId());
        assertEquals(items.get(0).getId(), dtos.get(0).getId());
        assertEquals(items.get(0).getName(), dtos.get(0).getName());
        assertEquals(items.get(0).getDescription(), dtos.get(0).getDescription());
        assertEquals(items.get(0).getAvailable(), dtos.get(0).getAvailable());
        assertEquals(1, dtos.get(0).getComments().size());
        assertEquals(comments.get(0).getText(), dtos.get(0).getComments().get(0).getText());
        assertEquals(comments.get(0).getCreated(), dtos.get(0).getComments().get(0).getCreated());
        assertEquals(comments.get(0).getAuthor().getName(), dtos.get(0).getComments().get(0).getAuthorName());
        assertNull(dtos.get(0).getLastBooking());
        assertNull(dtos.get(0).getNextBooking());
    }

    @Test
    void searchItemsByText_emptyList_whenNothingFounded() {
        String text = "mars";
        int from = 0;
        int size = 5;
        Paginator paginator = new Paginator(from, size);

        when(itemRepository.findItemByNameAndDesc(text, paginator)).thenReturn(Collections.emptyList());
        List<ItemDTO> dtos = List.copyOf(service.searchItemsByText(text, from, size));

        verify(itemRepository, atMostOnce()).findItemByNameAndDesc(text, paginator);
        assertEquals(0, dtos.size());
    }

    @Test
    void searchItemsByText_oneItem_whenFounded() {
        String text = "dollar";
        int from = 0;
        int size = 5;
        Paginator paginator = new Paginator(from, size);
        User owner = User.builder().id(86).name("jo").email("j@i.jo").build();
        Item item = Item.builder().id(12).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(itemRepository.findItemByNameAndDesc(text, paginator)).thenReturn(List.of(item));
        List<ItemDTO> dtos = List.copyOf(service.searchItemsByText(text, from, size));

        verify(itemRepository, atMostOnce()).findItemByNameAndDesc(text, paginator);
        assertEquals(1, dtos.size());
        assertEquals(item.getId(), dtos.get(0).getId());
        assertEquals(item.getName(), dtos.get(0).getName());
        assertEquals(item.getDescription(), dtos.get(0).getDescription());
        assertEquals(item.getAvailable(), dtos.get(0).getAvailable());
    }

    @Test
    void searchItemsByText_oneItem_whenPartOfNameAndIncorrectCase() {
        String text = "dOLl";
        int from = 0;
        int size = 5;
        Paginator paginator = new Paginator(from, size);
        User owner = User.builder().id(86).name("jo").email("j@i.jo").build();
        Item item = Item.builder().id(12).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(itemRepository.findItemByNameAndDesc(text.toLowerCase(), paginator)).thenReturn(List.of(item));
        List<ItemDTO> dtos = List.copyOf(service.searchItemsByText(text, from, size));

        verify(itemRepository, atMostOnce()).findItemByNameAndDesc(text, paginator);
        assertEquals(1, dtos.size());
        assertEquals(item.getId(), dtos.get(0).getId());
        assertEquals(item.getName(), dtos.get(0).getName());
        assertEquals(item.getDescription(), dtos.get(0).getDescription());
        assertEquals(item.getAvailable(), dtos.get(0).getAvailable());
    }

    @Test
    void addCommentToItemByUser_exception_whenItemNotPresented() {
        int itemID = 12;
        int userID = 84;
        IncomeCommentDTO incomeDTO = IncomeCommentDTO.builder().text("love money").build();

        when(itemRepository.existsById(itemID)).thenReturn(false);
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> service.addCommentToItemByUser(itemID, userID, incomeDTO));

        assertEquals("Item with ID 12 not present", exception.getMessage());
    }

    @Test
    void addCommentToItemByUser_exception_whenUserNotPresented() {
        int itemID = 11;
        int userID = 84;
        IncomeCommentDTO incomeDTO = IncomeCommentDTO.builder().text("love money").build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.addCommentToItemByUser(itemID, userID, incomeDTO));

        assertEquals("User with ID 84 not present", exception.getMessage());
    }

    @Test
    void addCommentToItemByUser_exception_whenUserNotBookAnything() {
        int itemID = 89;
        int userID = 23;
        IncomeCommentDTO incomeDTO = IncomeCommentDTO.builder().text("love money").build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findOneApprovedBookingOfUser(userID)).thenReturn(Optional.empty());
        UserNotBookedItemException exception = assertThrows(UserNotBookedItemException.class,
                () -> service.addCommentToItemByUser(itemID, userID, incomeDTO));

        assertEquals("User with ID 23 didn't book item", exception.getMessage());
    }

    @Test
    void addCommentToItemByUser_exception_whenItemNotBookedYet() {
        int itemID = 56;
        int userID = 83;
        IncomeCommentDTO incomeDTO = IncomeCommentDTO.builder().text("love money").build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findOneApprovedBookingOfUser(userID)).thenReturn(Optional.of(Booking.builder().build()));
        BookingValidationException exception = assertThrows(BookingValidationException.class,
                () -> service.addCommentToItemByUser(itemID, userID, incomeDTO));

        assertEquals("Item with ID 56 didn't book yet", exception.getMessage());
    }

    @Test
    void addCommentToItemByUser_correctAddComment() {
        int itemID = 32;
        int userID = 13;
        IncomeCommentDTO incomeDTO = IncomeCommentDTO.builder().text("love money").build();
        User owner = User.builder().id(userID).name("jo").email("j@i.jo").build();
        Item item = Item.builder().id(itemID).name("dollar").description("one dollar").owner(owner).available(true).build();

        when(itemRepository.existsById(itemID)).thenReturn(true);
        when(userRepository.existsById(userID)).thenReturn(true);
        when(bookingRepository.findOneApprovedBookingOfUser(userID)).thenReturn(Optional.of(Booking.builder().build()));
        when(bookingRepository.findOneApprovedBookingOfItemInPast(itemID, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))).thenReturn(Optional.of(Booking.builder().build()));
        when(userRepository.findById(userID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        OutcomeCommentDTO outcomeDTO = service.addCommentToItemByUser(itemID, userID, incomeDTO);

        assertEquals(incomeDTO.getText(), outcomeDTO.getText());
        assertEquals(owner.getName(), outcomeDTO.getAuthorName());
    }
}
