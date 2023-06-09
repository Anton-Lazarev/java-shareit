package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO create(@RequestHeader("X-Sharer-User-Id") int userID, @RequestBody ItemDTO itemDto) {
        log.info("Server : POST to /items from userID {} with {}", userID, itemDto.toString());
        return itemService.addItem(userID, itemDto);
    }

    @PostMapping("/{id}/comment")
    public OutcomeCommentDTO addComment(@RequestHeader("X-Sharer-User-Id") int userID, @PathVariable int id,
                                        @RequestBody IncomeCommentDTO commentDTO) {
        log.info("Server : POST to /items/{}/comment from userID {} with {}", id, userID, commentDTO.toString());
        return itemService.addCommentToItemByUser(id, userID, commentDTO);
    }

    @GetMapping
    public Collection<ItemWithBookingsAndCommentsDTO> findItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                       @RequestParam(defaultValue = "0") int from,
                                                                       @RequestParam(defaultValue = "5") int size) {
        log.info("Server : GET to /items from userID {} with from {} and size {}", userID, from, size);
        return itemService.getItemsOfUserByID(userID, from, size);
    }

    @PatchMapping("/{id}")
    public ItemDTO patch(@RequestHeader("X-Sharer-User-Id") int userID, @PathVariable int id, @RequestBody ItemDTO itemDto) {
        log.info("Server : PATCH to /items/{} from userID {} with {}", id, userID, itemDto.toString());
        return itemService.patchItem(userID, itemDto);
    }

    @GetMapping("/{id}")
    public ItemWithBookingsAndCommentsDTO findItemByID(@PathVariable int id, @RequestHeader("X-Sharer-User-Id") int userID) {
        log.info("Server : GET to /items/{} from userID {}", id, userID);
        return itemService.getItemByID(id, userID);
    }

    @GetMapping("/search")
    public Collection<ItemDTO> findItemsByText(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "5") int size) {
        log.info("Server : GET to /items/search with text {} , from {} , size {}", text, from, size);
        return itemService.searchItemsByText(text, from, size);
    }
}
