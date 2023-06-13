package ru.practicum.shareit.exceptions;

public class IncorrectOwnerInBookingException extends RuntimeException {
    public IncorrectOwnerInBookingException(String message) {
        super(message);
    }
}
