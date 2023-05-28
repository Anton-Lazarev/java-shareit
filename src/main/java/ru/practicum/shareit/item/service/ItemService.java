package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(int userID, ItemDto itemDto);

    ItemDto patchItem(int userID, ItemDto itemDto);

    ItemWithBookingsAndCommentsDTO getItemByID(int itemID, int userID);

    Collection<ItemWithBookingsAndCommentsDTO> getItemsOfUserByID(int userID);

    Collection<ItemDto> searchItemsByText(String text);

    OutcomeCommentDTO addCommentToItemByUser(int itemID, int userID, IncomeCommentDTO dto);
}
