package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;

import java.util.List;

public interface ItemRequestService {
    OutcomeItemRequestDTO addRequest(int userID, IncomeItemRequestDTO dto);

    List<OutcomeItemRequestWithItemsDTO> getRequestsOfUserByID(int userID);

    OutcomeItemRequestWithItemsDTO getRequestByID(int userID, int requestID);

    List<OutcomeItemRequestWithItemsDTO> getPageOfOtherUsersRequests(int userID, int from, int size);
}
