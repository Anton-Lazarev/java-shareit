package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.IncorrectOwnerInBookingException;

public class BookingValidator {
    public static void validate(Booking booking) {
        if (booking.getBooker().getId() == booking.getItem().getOwner().getId()) {
            throw new IncorrectOwnerInBookingException("Owner of item cannot book own item");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BookingValidationException("Start cannot be after end of booking");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BookingValidationException("Start cannot be equals to end of booking");
        }
    }
}
