package ru.practicum.shareit.exceptions;

public class IncorrectOwnerException extends RuntimeException {
    public IncorrectOwnerException(String message) {
        super(message);
    }
}
