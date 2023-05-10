package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRamRepository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemRamRepositoryTests {
    private ItemRamRepository repository;
    private Item firstItem;
    private Item secondItem;
    private User owner;

    @BeforeEach
    void beforeEach() {
        repository = new ItemRamRepository();
        owner = User.builder().id(1).name("Owner").email("owner@email.com").build();
        firstItem = Item.builder().name("Hammer").description("Cool hammer").owner(owner).available(true).build();
        secondItem = Item.builder().name("Bucket").description("Old bucket").owner(owner).available(true).build();
    }

    @Test
    void correctIdWhenAddItemAndGettingItemByID() {
        firstItem.setId(85);
        repository.addItem(firstItem);
        Item item = repository.getItemByID(1);

        assertEquals(1, item.getId());
        assertEquals("Hammer", item.getName());
        assertEquals("Cool hammer", item.getDescription());
        assertEquals("owner@email.com", item.getOwner().getEmail());
        assertTrue(item.getAvailable());
    }

    @Test
    void correctUpdatingItem() {
        repository.addItem(firstItem);
        Item update = Item.builder().id(1).name("Bubba").description("Hubba-Bubba").owner(owner).available(true).build();
        repository.update(update);

        Item item = repository.getItemByID(1);
        assertEquals(update.getName(), item.getName());
        assertEquals(update.getDescription(), item.getDescription());
        assertEquals(update.getAvailable(), item.getAvailable());
    }

    @Test
    void trueWhenIdPresentInBase() {
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        assertTrue(repository.containsID(2));
    }

    @Test
    void falseWhenIdNotPresentInBase() {
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        assertFalse(repository.containsID(854));
    }

    @Test
    void correctGettingItemsOfUser() {
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        ArrayList<Item> items = new ArrayList<>(repository.getItemsOfUserByUserID(1));
        assertEquals(2, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());

        assertEquals(2, items.get(1).getId());
        assertEquals("Bucket", items.get(1).getName());
        assertEquals("Old bucket", items.get(1).getDescription());
        assertTrue(items.get(1).getAvailable());
    }

    @Test
    void correctFindOneItemByPartName() {
        repository.addItem(firstItem);
        ArrayList<Item> items = new ArrayList<>(repository.findItemsByDescAndName("mer"));
        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
    }

    @Test
    void correctFindOneItemByPartNameWithAnotherRegister() {
        repository.addItem(firstItem);
        ArrayList<Item> items = new ArrayList<>(repository.findItemsByDescAndName("MeR"));
        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
    }

    @Test
    void correctFindItemsByPartNameAndDescriptionWithAnotherRegister() {
        secondItem.setDescription("Old bucket for hammer");
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        ArrayList<Item> items = new ArrayList<>(repository.findItemsByDescAndName("MeR"));
        assertEquals(2, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());

        assertEquals(2, items.get(1).getId());
        assertEquals("Bucket", items.get(1).getName());
        assertEquals("Old bucket for hammer", items.get(1).getDescription());
    }

    @Test
    void correctFindItemsByPartNameWithAnotherRegisterWhenOneUnavailable() {
        secondItem.setDescription("Old bucket for hammer");
        secondItem.setAvailable(false);
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        ArrayList<Item> items = new ArrayList<>(repository.findItemsByDescAndName("MeR"));

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
    }

    @Test
    void emptyListWhenMatchesAbsent() {
        repository.addItem(firstItem);
        repository.addItem(secondItem);
        ArrayList<Item> items = new ArrayList<>(repository.findItemsByDescAndName("qwe"));
        assertEquals(0, items.size());
    }
}
