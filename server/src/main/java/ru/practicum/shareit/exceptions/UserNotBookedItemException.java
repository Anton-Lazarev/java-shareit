package ru.practicum.shareit.exceptions;

public class UserNotBookedItemException extends RuntimeException {
    public UserNotBookedItemException(String message) {
        super(message);
    }
}
