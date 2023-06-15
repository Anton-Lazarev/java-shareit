package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.exceptions.BookingValidationException;

public class BookingDtoValidator {
    public static void validate(BookingDTO dto) {
        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new BookingValidationException("Start cannot be after end of booking");
        }
        if (dto.getStart().equals(dto.getEnd())) {
            throw new BookingValidationException("Start cannot be equals to end of booking");
        }
    }
}
