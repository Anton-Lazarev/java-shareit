package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.IncorrectOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRamRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRamRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemServiceTests {
    private UserRepository userRepository;
    private ItemService service;
    private User owner;
    private User anotherUser;
    private ItemDto firstDTO;
    private ItemDto secondDTO;

    @BeforeEach
    void beforeEach() {
        owner = User.builder().name("Owner").email("owner@rmail.com").build();
        anotherUser = User.builder().name("Another").email("another@rmail.com").build();
        userRepository = new UserRamRepository();
        service = new ItemServiceImpl(userRepository, new ItemRamRepository());
        userRepository.addUser(owner);
        userRepository.addUser(anotherUser);
        firstDTO = ItemDto.builder().name("Hammer").description("Cool hammer").available(true).build();
        secondDTO = ItemDto.builder().name("Bucket").description("Old bucket").available(true).build();
    }

    @Test
    void getExceptionWhenIncorrectUserIdInAddingItem() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.addItem(7456, firstDTO));
        assertEquals("User with ID 7456 not present", exception.getMessage());
    }

    @Test
    void correctAddingItemAndGettingByID() {
        service.addItem(1, secondDTO);
        ItemDto itemDto = service.getItemByID(1);

        assertEquals(1, itemDto.getId());
        assertEquals("Bucket", itemDto.getName());
        assertEquals("Old bucket", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void getExceptionWhenIncorrectIdOfItemInGettingByID() {
        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> service.getItemByID(8585));
        assertEquals("Item with ID 8585 not present", exception.getMessage());
    }

    @Test
    void correctAddingFewItemsAndGettingItemsOfUser() {
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.getItemsOfUserByID(1));

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
    void getExceptionWhenIncorrectUserIdInGettingItemsOfUser() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.getItemsOfUserByID(1245));
        assertEquals("User with ID 1245 not present", exception.getMessage());
    }

    @Test
    void getExceptionWhenIncorrectOwnerIdInPatchingItem() {
        service.addItem(1, secondDTO);
        ItemDto update = ItemDto.builder().id(1).name("Update").description("Updated hammer").available(true).build();
        final IncorrectOwnerException exception = assertThrows(IncorrectOwnerException.class, () -> service.patchItem(2, update));
        assertEquals("User with ID 2 not owner of present item with ID 1", exception.getMessage());
    }

    @Test
    void correctUpdateAllFieldsInItem() {
        service.addItem(1, secondDTO);
        ItemDto update = ItemDto.builder().id(1).name("Update").description("Updated bucket").available(true).build();
        service.patchItem(1, update);
        ItemDto updatedItem = service.getItemByID(1);

        assertEquals(1, updatedItem.getId());
        assertEquals("Update", updatedItem.getName());
        assertEquals("Updated bucket", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void correctUpdateOnlyNameInItem() {
        service.addItem(1, secondDTO);
        ItemDto update = ItemDto.builder().id(1).name("Update").build();
        service.patchItem(1, update);
        ItemDto updatedItem = service.getItemByID(1);

        assertEquals(1, updatedItem.getId());
        assertEquals("Update", updatedItem.getName());
        assertEquals("Old bucket", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void correctUpdateOnlyDescriptionInItem() {
        service.addItem(1, secondDTO);
        ItemDto update = ItemDto.builder().id(1).description("Updated bucket").build();
        service.patchItem(1, update);
        ItemDto updatedItem = service.getItemByID(1);

        assertEquals(1, updatedItem.getId());
        assertEquals("Bucket", updatedItem.getName());
        assertEquals("Updated bucket", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void correctUpdateOnlyAvailableInItem() {
        service.addItem(1, secondDTO);
        ItemDto update = ItemDto.builder().id(1).available(false).build();
        service.patchItem(1, update);
        ItemDto updatedItem = service.getItemByID(1);

        assertEquals(1, updatedItem.getId());
        assertEquals("Bucket", updatedItem.getName());
        assertEquals("Old bucket", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void getExceptionWhenIncorrectIdInSearchItemsByText() {
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.getItemsOfUserByID(3654));
        assertEquals("User with ID 3654 not present", exception.getMessage());
    }

    @Test
    void correctFindItemsByTextInNameAndDesc() {
        secondDTO.setDescription("Hammer clone");
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("mmer"));
        assertEquals(2, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());

        assertEquals(2, items.get(1).getId());
        assertEquals("Bucket", items.get(1).getName());
        assertEquals("Hammer clone", items.get(1).getDescription());
        assertTrue(items.get(1).getAvailable());
    }

    @Test
    void correctFindItemsByTextInNameAndDescWithAnotherRegister() {
        secondDTO.setDescription("Hammer clone");
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("mMEr"));
        assertEquals(2, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());

        assertEquals(2, items.get(1).getId());
        assertEquals("Bucket", items.get(1).getName());
        assertEquals("Hammer clone", items.get(1).getDescription());
        assertTrue(items.get(1).getAvailable());
    }

    @Test
    void correctFindItemsByTextInNameAndDescWithAnotherRegisterAndDifferentOwners() {
        secondDTO.setDescription("Hammer clone");
        service.addItem(1, firstDTO);
        service.addItem(2, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("mMEr"));
        assertEquals(2, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());

        assertEquals(2, items.get(1).getId());
        assertEquals("Bucket", items.get(1).getName());
        assertEquals("Hammer clone", items.get(1).getDescription());
        assertTrue(items.get(1).getAvailable());
    }

    @Test
    void correctFindItemsByTextInNameAndDescWithAnotherRegisterWithUnavailable() {
        secondDTO.setDescription("Hammer clone");
        firstDTO.setAvailable(false);
        service.addItem(1, firstDTO);
        service.addItem(2, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("mMEr"));
        assertEquals(1, items.size());

        assertEquals(2, items.get(0).getId());
        assertEquals("Bucket", items.get(0).getName());
        assertEquals("Hammer clone", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
    }

    @Test
    void correctFindItemsByTextInDescWithAnotherRegister() {
        secondDTO.setDescription("Hammer clone");
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("CooL"));
        assertEquals(1, items.size());

        assertEquals(1, items.get(0).getId());
        assertEquals("Hammer", items.get(0).getName());
        assertEquals("Cool hammer", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
    }

    @Test
    void emptyListWhenMatchesAbsent() {
        secondDTO.setDescription("Hammer clone");
        service.addItem(1, firstDTO);
        service.addItem(1, secondDTO);
        ArrayList<ItemDto> items = new ArrayList<>(service.searchItemsByText("trgfhf"));
        assertEquals(0, items.size());
    }
}
