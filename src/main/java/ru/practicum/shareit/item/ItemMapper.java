package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

@UtilityClass
public class ItemMapper {
    public ItemDTO itemToItemDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(setupRequestID(item))
                .build();
    }

    public Item itemDtoToItem(ItemDTO dto, User owner) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .build();
    }

    public Item itemDtoToItem(ItemDTO dto, User owner, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public ShortItem itemToShortItem(Item item) {
        return ShortItem.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public ItemWithBookingsAndCommentsDTO itemToItemWithBookingsAndCommentsDTO(Item item) {
        return ItemWithBookingsAndCommentsDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
    }

    private int setupRequestID(Item item) {
        if (item.getRequest() == null) {
            return 0;
        } else {
            return item.getRequest().getId();
        }
    }
}
