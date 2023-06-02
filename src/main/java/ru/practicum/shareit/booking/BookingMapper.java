package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking incomeBookingDtoToBooking(IncomeBookingDTO dto, User booker, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static OutcomeBookingDTO bookingToOutcomeBookingDTO(Booking booking) {
        return OutcomeBookingDTO.builder()
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
