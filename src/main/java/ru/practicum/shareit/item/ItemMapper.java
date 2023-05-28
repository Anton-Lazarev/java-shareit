package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ShortItem;

import java.util.Collections;

public class ItemMapper {
    public static ItemDto itemToItemDTO(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item itemDtoToItem(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public static ShortItem itemToShortItem(Item item) {
        return ShortItem.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemWithBookingsAndCommentsDTO itemToItemWithBookingsAndCommentsDTO(Item item) {
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
}
