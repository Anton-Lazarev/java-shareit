package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.OutcomeItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutcomeItemRequestDtoJsonTests {
    @Autowired
    private JacksonTester<OutcomeItemRequestDTO> json;

    @SneakyThrows
    @Test
    void correct_transformItemRequestDtoToJSON() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        User requestor = User.builder().id(41).name("Jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder().id(12).requestor(requestor).created(created).description("need a dollar").build();

        OutcomeItemRequestDTO dto = RequestMapper.itemRequestToOutcomeRequestDTO(request);
        JsonContent<OutcomeItemRequestDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }
}
