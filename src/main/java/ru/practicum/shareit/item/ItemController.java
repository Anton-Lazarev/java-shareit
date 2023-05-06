package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userID, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userID, itemDto);
    }

    @GetMapping
    public Collection<ItemDto> findItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userID) {
        return itemService.getItemsOfUserByID(userID);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") int userID, @PathVariable int id, @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        return itemService.patchItem(userID, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto findItemByID(@PathVariable int id) {
        return itemService.getItemByID(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }
}
