package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
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

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public OutcomeItemRequestDTO create(@RequestHeader("X-Sharer-User-Id") int userID,
                                        @Valid @RequestBody IncomeItemRequestDTO dto) {
        return requestService.addRequest(userID, dto);
    }

    @GetMapping
    public List<OutcomeItemRequestWithItemsDTO> getOwnRequests(@RequestHeader("X-Sharer-User-Id") int userID) {
        return requestService.getRequestsOfUserByID(userID);
    }

    @GetMapping("/{requestId}")
    public OutcomeItemRequestWithItemsDTO findRequestByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                                          @PathVariable int requestId) {
        return requestService.getRequestByID(userID, requestId);
    }

    @GetMapping("/all")
    public List<OutcomeItemRequestWithItemsDTO> getPageOfItemRequests(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                      @RequestParam(defaultValue = "0") int from,
                                                                      @RequestParam(defaultValue = "5") int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Page or size can't be negative");
        }
        return requestService.getPageOfOtherUsersRequests(userID, from, size);
    }
}
