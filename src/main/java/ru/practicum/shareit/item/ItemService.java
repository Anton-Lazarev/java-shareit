package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemDto addItem(int userID, ItemDto itemDto) {
        if (!userStorage.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item newItem = ItemMapper.itemDtoToItem(itemDto);
        newItem.setOwner(userStorage.findUserByID(userID));
        itemStorage.addItem(newItem);
        log.info("Create new item with ID {}, name {} and owner ID {}", newItem.getId(), newItem.getName(), userID);
        return ItemMapper.itemToItemDTO(newItem);
    }

    public ItemDto patchItem(int userID, ItemDto itemDto) {
        if (!userStorage.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item item = itemStorage.getItemByID(itemDto.getId());
        if (item.getOwner().getId() != userID) {
            throw new IncorrectOwnerException("User with ID " + userID + " not owner of present item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemStorage.update(item);
        log.info("Item with ID {} updated", item.getId());
        return ItemMapper.itemToItemDTO(item);
    }

    public ItemDto getItemByID(int itemID) {
        if (!itemStorage.containsID(itemID)) {
            throw new ItemNotFoundException("Item with ID " + itemID + " not present");
        }
        log.info("Getting item with ID {}", itemID);
        return ItemMapper.itemToItemDTO(itemStorage.getItemByID(itemID));
    }

    public Collection<ItemDto> getItemsOfUserByID(int userID) {
        ArrayList<ItemDto> itemsDTO = new ArrayList<>();
        if (!userStorage.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        for (Item item : itemStorage.getItemsOfUserByUserID(userID)) {
            itemsDTO.add(ItemMapper.itemToItemDTO(item));
        }
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }

    public Collection<ItemDto> searchItemsByText(String text) {
        ArrayList<ItemDto> itemsDTO = new ArrayList<>();
        if (text.isEmpty() || text.isBlank()) {
            return itemsDTO;
        }
        for (Item item : itemStorage.findItemsByDescAndName(text)) {
            itemsDTO.add(ItemMapper.itemToItemDTO(item));
        }
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }
}
