package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectItemOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotBookedItemException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(int userID, ItemDto itemDto) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item newItem = ItemMapper.itemDtoToItem(itemDto, userRepository.findById(userID).get());
        itemRepository.save(newItem);
        log.info("Create new item with ID {}, name {} and owner ID {}", newItem.getId(), newItem.getName(), userID);
        return ItemMapper.itemToItemDTO(newItem);
    }

    @Override
    public ItemDto patchItem(int userID, ItemDto itemDto) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item item = itemRepository.findById(itemDto.getId()).get();
        if (item.getOwner().getId() != userID) {
            throw new IncorrectItemOwnerException("User with ID " + userID + " not owner of current item with ID " + item.getId());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        log.info("Item with ID {} updated", item.getId());
        return ItemMapper.itemToItemDTO(item);
    }

    @Override
    public ItemWithBookingsAndCommentsDTO getItemByID(int itemID, int userID) {
        if (!itemRepository.existsById(itemID)) {
            throw new ItemNotFoundException("Item with ID " + itemID + " not present");
        }
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item item = itemRepository.findById(itemID).get();
        log.info("Getting item with ID {}", itemID);
        if (item.getOwner().getId() == userID) {
            return createOutcomeItemDtoWithBookingsAndComments(item);
        }
        return createOutcomeItemDtoOnlyWithComments(itemRepository.findById(itemID).get());
    }

    @Override
    public Collection<ItemWithBookingsAndCommentsDTO> getItemsOfUserByID(int userID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<Item> itemsOfUser = itemRepository.findAllByUserId(userID);
        List<ItemWithBookingsAndCommentsDTO> itemsDTO = new ArrayList<>();
        for (Item item : itemsOfUser) {
            ItemWithBookingsAndCommentsDTO dto = createOutcomeItemDtoWithBookingsAndComments(item);
            itemsDTO.add(dto);
        }
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        List<ItemDto> itemsDTO = itemRepository.findItemByNameAndDesc(text.toLowerCase())
                .stream().map(ItemMapper::itemToItemDTO)
                .collect(Collectors.toList());
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }

    @Override
    public OutcomeCommentDTO addCommentToItemByUser(int itemID, int userID, IncomeCommentDTO dto) {
        if (!itemRepository.existsById(itemID)) {
            throw new ItemNotFoundException("Item with ID " + itemID + " not present");
        }
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (bookingRepository.findOneApprovedBookingOfUser(userID).isEmpty()) {
            throw new UserNotBookedItemException("User with ID " + userID + " didn't book item");
        }
        if (bookingRepository.findOneApprovedBookingOfItemInPast(itemID, LocalDateTime.now()).isEmpty()) {
            throw new BookingValidationException("Item with ID " + itemID + " didn't book yet");
        }
        Comment newComment = CommentMapper.incomeCommentDtoToComment(dto,
                userRepository.findById(userID).get(),
                itemRepository.findById(itemID).get());
        commentRepository.save(newComment);
        log.info("Add new comment with ID {} to item with ID {} by user with ID {}", newComment.getId(), itemID, userID);
        return CommentMapper.commentToOutcomeCommentDTO(newComment);
    }

    private ItemWithBookingsAndCommentsDTO createOutcomeItemDtoWithBookingsAndComments(Item item) {
        Optional<Booking> last = bookingRepository.findPreviousItemBooking(item.getId(), LocalDateTime.now());
        Optional<Booking> next = bookingRepository.findNextItemBooking(item.getId(), LocalDateTime.now());
        ItemWithBookingsAndCommentsDTO dto = ItemMapper.itemToItemWithBookingsAndCommentsDTO(item);
        if (last.isEmpty()) {
            dto.setLastBooking(null);
        } else {
            dto.setLastBooking(BookingMapper.bookingToShortBooking(last.get()));
        }
        if (next.isEmpty()) {
            dto.setNextBooking(null);
        } else {
            dto.setNextBooking(BookingMapper.bookingToShortBooking(next.get()));
        }
        dto.setComments(collectOutcomeCommentsDtoOfItemByID(item.getId()));
        return dto;
    }

    private ItemWithBookingsAndCommentsDTO createOutcomeItemDtoOnlyWithComments(Item item) {
        ItemWithBookingsAndCommentsDTO dto = ItemMapper.itemToItemWithBookingsAndCommentsDTO(item);
        dto.setComments(collectOutcomeCommentsDtoOfItemByID(item.getId()));
        return dto;
    }

    private List<OutcomeCommentDTO> collectOutcomeCommentsDtoOfItemByID(int itemID) {
        List<Comment> comments = commentRepository.findAllByItemID(itemID);
        List<OutcomeCommentDTO> outcomeCommentDTOS;
        if (comments.isEmpty()) {
            outcomeCommentDTOS = new ArrayList<>();
        } else {
            outcomeCommentDTOS = comments.stream()
                    .map(CommentMapper::commentToOutcomeCommentDTO)
                    .collect(Collectors.toList());
        }
        return outcomeCommentDTOS;
    }
}
