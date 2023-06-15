package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutcomeCommentDtoJsonTests {
    @Autowired
    private JacksonTester<OutcomeCommentDTO> json;

    @SneakyThrows
    @Test
    void correct_transformOutcomeCommentDtoToJSON() {
        LocalDateTime moment = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();
        Comment comment = Comment.builder().id(11).created(moment).author(user).text("cool dollar").item(item).build();

        OutcomeCommentDTO dto = CommentMapper.commentToOutcomeCommentDTO(comment);
        JsonContent<OutcomeCommentDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
    }
}
