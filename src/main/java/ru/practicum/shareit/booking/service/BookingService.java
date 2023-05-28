package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.IncomeBookingDto;
import ru.practicum.shareit.booking.dto.OutcomeBookingDto;

import java.util.List;

public interface BookingService {
    OutcomeBookingDto addBooking(int userID, IncomeBookingDto bookingDto);

    OutcomeBookingDto changeBookingStatus(int userID, int bookingID, boolean approve);

    OutcomeBookingDto getBookingByID(int userID, int bookingID);

    List<OutcomeBookingDto> getBookingsOfUserByState(int userID, String status);

    List<OutcomeBookingDto> getBookingsOfUserItemsByState(int userID, String status);
}
