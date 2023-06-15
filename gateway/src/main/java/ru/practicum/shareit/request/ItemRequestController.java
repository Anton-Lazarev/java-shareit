package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.PageValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userID,
                                         @Valid @RequestBody ItemRequestDTO dto) {
        log.info("Gateway : POST to /requests from userID {} and {}", userID, dto.toString());
        return requestClient.addRequest(userID, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") int userID) {
        log.info("Gateway : GET to /requests from userID {}", userID);
        return requestClient.getRequestsOfUserByID(userID);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                                  @PathVariable int requestId) {
        log.info("Gateway : GET to /requests/{} from userID {}", requestId, userID);
        return requestClient.getRequestByID(userID, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPageOfItemRequests(@RequestHeader("X-Sharer-User-Id") int userID,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "5") int size) {
        if (from < 0 || size <= 0) {
            throw new PageValidationException("Page or size can't be negative");
        }
        log.info("Gateway : GET to /requests/all from userID {} with from {} and size {}", userID, from, size);
        return requestClient.getPageOfOtherUsersRequests(userID, from, size);
    }
}
