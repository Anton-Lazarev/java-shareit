package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ShortBookingJsonTests {
    @Autowired
    private JacksonTester<ShortBooking> json;

    @SneakyThrows
    @Test
    void correct_transferShortBookingToJSON() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 11, 14, 21, 54);
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder().id(98).booker(user).item(item).start(start).end(end).status(BookingStatus.WAITING).build();

        ShortBooking shortBooking = BookingMapper.bookingToShortBooking(booking);
        JsonContent<ShortBooking> result = json.write(shortBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(shortBooking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(shortBooking.getBookerId());
    }
}
