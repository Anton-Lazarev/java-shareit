package ru.practicum.shareit.exceptions;

public class IncorrectItemOwnerException extends RuntimeException {
    public IncorrectItemOwnerException(String message) {
        super(message);
    }
}
