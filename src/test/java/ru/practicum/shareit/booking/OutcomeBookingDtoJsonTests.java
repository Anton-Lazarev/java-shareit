package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutcomeBookingDtoJsonTests {
    @Autowired
    private JacksonTester<OutcomeBookingDTO> json;

    @SneakyThrows
    @Test
    void correct_transferOutcomeBookingDtoToJson() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 11, 14, 21, 54);
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();
        Booking booking = Booking.builder().id(98).booker(user).item(item).start(start).end(end).status(BookingStatus.WAITING).build();

        OutcomeBookingDTO dto = BookingMapper.bookingToOutcomeBookingDTO(booking);
        JsonContent<OutcomeBookingDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(booking.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(booking.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(booking.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(booking.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(user.getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
    }
}
