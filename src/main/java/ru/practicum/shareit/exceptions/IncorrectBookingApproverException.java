package ru.practicum.shareit.exceptions;

public class IncorrectBookingApproverException extends RuntimeException {
    public IncorrectBookingApproverException(String message) {
        super(message);
    }
}
