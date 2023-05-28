package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.IncomeBookingDto;
import ru.practicum.shareit.booking.dto.OutcomeBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ShortBooking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static Booking incomeBookingDtoToBooking(IncomeBookingDto dto) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }

    public static OutcomeBookingDto bookingToOutcomeBookingDTO(Booking booking) {
        return OutcomeBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.userToShortUser(booking.getBooker()))
                .item(ItemMapper.itemToShortItem(booking.getItem()))
                .build();
    }

    public static ShortBooking bookingToShortBooking(Booking booking) {
        return ShortBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
