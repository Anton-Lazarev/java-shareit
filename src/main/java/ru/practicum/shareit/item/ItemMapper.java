package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto itemToItemDTO(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item itemDtoToItem(ItemDto dto, User owner) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
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
