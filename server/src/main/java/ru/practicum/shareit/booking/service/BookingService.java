package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;

import java.util.List;

public interface BookingService {
    OutcomeBookingDTO addBooking(int userID, IncomeBookingDTO bookingDto);

    OutcomeBookingDTO changeBookingStatus(int userID, int bookingID, boolean approve);

    OutcomeBookingDTO getBookingByID(int userID, int bookingID);

    List<OutcomeBookingDTO> getBookingsOfUserByState(int userID, String status, int from, int size);

    List<OutcomeBookingDTO> getBookingsOfUserItemsByState(int userID, String status, int from, int size);
}
