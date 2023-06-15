package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public OutcomeItemRequestDTO create(@RequestHeader("X-Sharer-User-Id") int userID,
                                        @RequestBody IncomeItemRequestDTO dto) {
        log.info("Server : POST to /requests from userID {} and {}", userID, dto.toString());
        return requestService.addRequest(userID, dto);
    }

    @GetMapping
    public List<OutcomeItemRequestWithItemsDTO> getOwnRequests(@RequestHeader("X-Sharer-User-Id") int userID) {
        log.info("Server : GET to /requests from userID {}", userID);
        return requestService.getRequestsOfUserByID(userID);
    }

    @GetMapping("/{requestId}")
    public OutcomeItemRequestWithItemsDTO findRequestByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                                          @PathVariable int requestId) {
        log.info("Server : GET to /requests/{} from userID {}", requestId, userID);
        return requestService.getRequestByID(userID, requestId);
    }

    @GetMapping("/all")
    public List<OutcomeItemRequestWithItemsDTO> getPageOfItemRequests(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                      @RequestParam(defaultValue = "0") int from,
                                                                      @RequestParam(defaultValue = "5") int size) {
        log.info("Server : GET to /requests/all from userID {} with from {} and size {}", userID, from, size);
        return requestService.getPageOfOtherUsersRequests(userID, from, size);
    }
}
