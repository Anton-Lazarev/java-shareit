package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(int userID, ItemDto itemDto);

    ItemDto patchItem(int userID, ItemDto itemDto);

    ItemDto getItemByID(int itemID);

    Collection<ItemDto> getItemsOfUserByID(int userID);

    Collection<ItemDto> searchItemsByText(String text);
}
