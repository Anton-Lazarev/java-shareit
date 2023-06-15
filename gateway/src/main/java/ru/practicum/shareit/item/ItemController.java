package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.PageValidationException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userID, @Valid @RequestBody ItemDTO itemDto) {
        log.info("Gateway : POST to /items from userID {} with {}", userID, itemDto.toString());
        return itemClient.addItem(userID, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") int userID, @PathVariable int id,
                                             @Valid @RequestBody CommentDTO commentDTO) {
        log.info("Gateway : POST to /items/{}/comment from userID {} with {}", id, userID, commentDTO.toString());
        return itemClient.addCommentToItemByUser(id, userID, commentDTO);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByOwner(@RequestHeader("X-Sharer-User-Id") int userID,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "5") int size) {
        if (from < 0 || size <= 0) {
            throw new PageValidationException("Page or size can't be negative");
        }
        log.info("Gateway : GET to /items from userID {} with from {} and size {}", userID, from, size);
        return itemClient.getItemsOfUserByID(userID, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@RequestHeader("X-Sharer-User-Id") int userID, @PathVariable int id, @RequestBody ItemDTO itemDto) {
        log.info("Gateway : PATCH to /items/{} from userID {} with {}", id, userID, itemDto.toString());
        itemDto.setId(id);
        return itemClient.patchItem(userID, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemByID(@PathVariable int id, @RequestHeader("X-Sharer-User-Id") int userID) {
        log.info("Gateway : GET to /items/{} from userID {}", id, userID);
        return itemClient.getItemByID(userID, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestHeader("X-Sharer-User-Id") int userID,
                                                  @RequestParam String text,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "5") int size) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        if (from < 0 || size <= 0) {
            throw new PageValidationException("Page or size can't be negative");
        }
        log.info("Gateway : GET to /items/search with text {} , from {} , size {}", text, from, size);
        return itemClient.searchItemsByText(userID, text, from, size);
    }
}
