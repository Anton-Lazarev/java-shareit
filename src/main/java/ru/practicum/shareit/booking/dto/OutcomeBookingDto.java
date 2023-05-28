package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.ShortItem;
import ru.practicum.shareit.user.model.ShortUser;

import java.time.LocalDateTime;

@Data
@Builder
public class OutcomeBookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ShortUser booker;
    private ShortItem item;
    private BookingStatus status;
}
