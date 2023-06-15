package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortBooking {
    private int id;
    private int bookerId;
}
