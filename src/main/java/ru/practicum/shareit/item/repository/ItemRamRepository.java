package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component("ItemRamStorage")
public class ItemRamRepository implements ItemRepository {
    private int nextID = 1;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(nextID);
        nextID++;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Item getItemByID(int id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> findItemsByDescAndName(String text) {
        Predicate<Item> textInName = item -> item.getName().toLowerCase().contains(text.toLowerCase());
        Predicate<Item> textInDescription = item -> item.getDescription().toLowerCase().contains(text.toLowerCase());
        Collection<Item> itemsWithText = items.values().stream().filter(textInName.or(textInDescription)).collect(Collectors.toList());
        return itemsWithText.stream().filter(Item::getAvailable).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsOfUserByUserID(int id) {
        return items.values().stream().filter(item -> item.getOwner().getId() == id).collect(Collectors.toList());
    }

    @Override
    public boolean containsID(int id) {
        return items.containsKey(id);
    }
}
