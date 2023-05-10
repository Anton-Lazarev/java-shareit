package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository {

    Item addItem(Item item);

    void update(Item item);

    Item getItemByID(int id);

    Collection<Item> findItemsByDescAndName(String text);

    Collection<Item> getItemsOfUserByUserID(int id);

    boolean containsID(int id);
}
