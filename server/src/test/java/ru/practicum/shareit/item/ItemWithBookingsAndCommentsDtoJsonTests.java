package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingsAndCommentsDtoJsonTests {
    @Autowired
    private JacksonTester<ItemWithBookingsAndCommentsDTO> json;

    @SneakyThrows
    @Test
    void correct_transferItemDtoWithoutCommentsAndBookingsToJSON() {
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();

        ItemWithBookingsAndCommentsDTO dto = ItemMapper.itemToItemWithBookingsAndCommentsDTO(item);
        JsonContent<ItemWithBookingsAndCommentsDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @SneakyThrows
    @Test
    void correct_transferItemDtoWithoutCommentsToJSON() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 11, 14, 21, 54);
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();
        Booking previousBooking = Booking.builder().id(98).booker(user).item(item).start(start).end(end).status(BookingStatus.REJECTED).build();
        Booking nextBooking = Booking.builder().id(98).booker(user).item(item).start(start.plusDays(1)).end(end.plusDays(1)).status(BookingStatus.APPROVED).build();

        ItemWithBookingsAndCommentsDTO dto = ItemMapper.itemToItemWithBookingsAndCommentsDTO(item);
        dto.setLastBooking(BookingMapper.bookingToShortBooking(previousBooking));
        dto.setNextBooking(BookingMapper.bookingToShortBooking(nextBooking));
        JsonContent<ItemWithBookingsAndCommentsDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(previousBooking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(previousBooking.getBooker().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBooking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(nextBooking.getBooker().getId());
    }

    @SneakyThrows
    @Test
    void correct_transferItemDtoWithCommentsAndBookingsToJSON() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 11, 14, 21, 54);
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();
        Booking previousBooking = Booking.builder().id(98).booker(user).item(item).start(start).end(end).status(BookingStatus.REJECTED).build();
        Booking nextBooking = Booking.builder().id(98).booker(user).item(item).start(start.plusDays(1)).end(end.plusDays(1)).status(BookingStatus.APPROVED).build();
        OutcomeCommentDTO comment = CommentMapper.commentToOutcomeCommentDTO(Comment.builder()
                .id(76)
                .item(item)
                .author(user)
                .text("cool dollar")
                .created(end.plusDays(1))
                .build());

        ItemWithBookingsAndCommentsDTO dto = ItemMapper.itemToItemWithBookingsAndCommentsDTO(item);
        dto.setLastBooking(BookingMapper.bookingToShortBooking(previousBooking));
        dto.setNextBooking(BookingMapper.bookingToShortBooking(nextBooking));
        dto.setComments(List.of(comment));
        JsonContent<ItemWithBookingsAndCommentsDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(previousBooking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(previousBooking.getBooker().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBooking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(nextBooking.getBooker().getId());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNotEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(comment.getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(comment.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(comment.getCreated().toString());
    }
}
