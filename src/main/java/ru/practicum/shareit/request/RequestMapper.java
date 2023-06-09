package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.request.dto.IncomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class RequestMapper {
    public static ItemRequest incomeDtoToItemRequest(IncomeItemRequestDTO dto, User user) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(user)
                .created(dto.getCreated())
                .build();
    }

    public static OutcomeItemRequestDTO itemRequestToOutcomeRequestDTO(ItemRequest request) {
        return OutcomeItemRequestDTO.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static OutcomeItemRequestWithItemsDTO itemRequestToOutcomeRequestWithItemsDTO(ItemRequest request,
                                                                                         List<ItemDTO> items) {
        return OutcomeItemRequestWithItemsDTO.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
