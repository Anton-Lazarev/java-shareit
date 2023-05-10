package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectOwnerException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(int userID, ItemDto itemDto) {
        if (!userRepository.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item newItem = ItemMapper.itemDtoToItem(itemDto);
        newItem.setOwner(userRepository.findUserByID(userID));
        itemRepository.addItem(newItem);
        log.info("Create new item with ID {}, name {} and owner ID {}", newItem.getId(), newItem.getName(), userID);
        return ItemMapper.itemToItemDTO(newItem);
    }

    @Override
    public ItemDto patchItem(int userID, ItemDto itemDto) {
        if (!userRepository.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        Item item = itemRepository.getItemByID(itemDto.getId());
        if (item.getOwner().getId() != userID) {
            throw new IncorrectOwnerException("User with ID " + userID + " not owner of present item with ID " + item.getId());
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
        itemRepository.update(item);
        log.info("Item with ID {} updated", item.getId());
        return ItemMapper.itemToItemDTO(item);
    }

    @Override
    public ItemDto getItemByID(int itemID) {
        if (!itemRepository.containsID(itemID)) {
            throw new ItemNotFoundException("Item with ID " + itemID + " not present");
        }
        log.info("Getting item with ID {}", itemID);
        return ItemMapper.itemToItemDTO(itemRepository.getItemByID(itemID));
    }

    @Override
    public Collection<ItemDto> getItemsOfUserByID(int userID) {
        if (!userRepository.containsID(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<ItemDto> itemsDTO = itemRepository.getItemsOfUserByUserID(userID)
                .stream().map(ItemMapper::itemToItemDTO)
                .collect(Collectors.toList());
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        List<ItemDto> itemsDTO = itemRepository.findItemsByDescAndName(text)
                .stream().map(ItemMapper::itemToItemDTO)
                .collect(Collectors.toList());
        log.info("Get itemsDTO list with size {}", itemsDTO.size());
        return itemsDTO;
    }
}
