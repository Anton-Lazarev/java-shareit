package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;

import java.util.Collection;

public interface ItemService {
    ItemDTO addItem(int userID, ItemDTO itemDto);

    ItemDTO patchItem(int userID, ItemDTO itemDto);

    ItemWithBookingsAndCommentsDTO getItemByID(int itemID, int userID);

    Collection<ItemWithBookingsAndCommentsDTO> getItemsOfUserByID(int userID, int from, int size);

    Collection<ItemDTO> searchItemsByText(String text, int from, int size);

    OutcomeCommentDTO addCommentToItemByUser(int itemID, int userID, IncomeCommentDTO dto);
}
