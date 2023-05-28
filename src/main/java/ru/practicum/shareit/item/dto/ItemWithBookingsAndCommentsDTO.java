package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.ShortBooking;

import java.util.List;

@Data
@Builder
public class ItemWithBookingsAndCommentsDTO {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBooking nextBooking;
    private ShortBooking lastBooking;
    private List<OutcomeCommentDTO> comments;
}
