package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortBooking {
    int id;
    int bookerId;
}
