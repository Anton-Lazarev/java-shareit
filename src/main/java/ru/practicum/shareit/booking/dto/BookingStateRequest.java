package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingStateRequest {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingStateRequest> from(String stringState) {
        for (BookingStateRequest state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
