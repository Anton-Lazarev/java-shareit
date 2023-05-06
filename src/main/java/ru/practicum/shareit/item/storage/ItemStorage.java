package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemStorage {

    Item addItem(Item item);

    void update(Item item);

    Item getItemByID(int id);

    Collection<Item> findItemsByDescAndName(String text);

    Collection<Item> getItemsOfUserByUserID(int id);

    boolean containsID(int id);
}
