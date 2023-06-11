package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Paginator;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public OutcomeItemRequestDTO addRequest(int userID, IncomeItemRequestDTO dto) {
        dto.setCreated(LocalDateTime.now());
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        ItemRequest newRequest = RequestMapper.incomeDtoToItemRequest(dto, userRepository.findById(userID).get());
        requestRepository.save(newRequest);
        log.info("Create new request with ID {} from user with ID {}", newRequest.getId(), userID);
        return RequestMapper.itemRequestToOutcomeRequestDTO(newRequest);
    }

    @Override
    public List<OutcomeItemRequestWithItemsDTO> getRequestsOfUserByID(int userID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<OutcomeItemRequestWithItemsDTO> outcomeDTOs = new ArrayList<>();
        for (ItemRequest request : requestRepository.findAllByUserID(userID)) {
            outcomeDTOs.add(RequestMapper.itemRequestToOutcomeRequestWithItemsDTO(request,
                    prepareItemsForRequestDTO(request.getId())));
        }
        log.info("Get requests list of size {} with items", outcomeDTOs.size());
        return outcomeDTOs;
    }

    @Override
    public OutcomeItemRequestWithItemsDTO getRequestByID(int userID, int requestID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        if (!requestRepository.existsById(requestID)) {
            throw new ItemRequestNotFoundException("Item request with ID " + requestID + " not presented");
        }
        return RequestMapper.itemRequestToOutcomeRequestWithItemsDTO(
                requestRepository.findById(requestID).get(),
                prepareItemsForRequestDTO(requestID));
    }

    @Override
    public List<OutcomeItemRequestWithItemsDTO> getPageOfOtherUsersRequests(int userID, int from, int size) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User with ID " + userID + " not present");
        }
        List<OutcomeItemRequestWithItemsDTO> outcomeDTOs = new ArrayList<>();
        for (ItemRequest request : requestRepository.findAllFromAnotherUsers(userID, new Paginator(from, size))) {
            outcomeDTOs.add(RequestMapper.itemRequestToOutcomeRequestWithItemsDTO(request,
                    prepareItemsForRequestDTO(request.getId())));
        }
        log.info("Get requests list of size {} with items", outcomeDTOs.size());
        return outcomeDTOs;
    }

    private List<ItemDTO> prepareItemsForRequestDTO(int requestID) {
        return itemRepository.findAllByRequestID(requestID)
                .stream().map(ItemMapper::itemToItemDTO)
                .collect(Collectors.toList());
    }
}
