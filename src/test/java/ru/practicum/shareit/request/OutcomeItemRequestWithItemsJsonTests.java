package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.OutcomeItemRequestWithItemsDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OutcomeItemRequestWithItemsJsonTests {
    @Autowired
    private JacksonTester<OutcomeItemRequestWithItemsDTO> json;

    @SneakyThrows
    @Test
    void correct_transferDtoToJsonWithoutItems() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        User requestor = User.builder().id(41).name("Jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder().id(12).requestor(requestor).created(created).description("need a dollar").build();
        List<ItemDTO> itemDTOS = Collections.emptyList();

        OutcomeItemRequestWithItemsDTO dto = RequestMapper.itemRequestToOutcomeRequestWithItemsDTO(request, itemDTOS);
        JsonContent<OutcomeItemRequestWithItemsDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @SneakyThrows
    @Test
    void correct_transferDtoToJsonWithItems() {
        LocalDateTime created = LocalDateTime.of(2023, 6, 9, 12, 56, 30);
        User requestor = User.builder().id(41).name("Jo").email("j@i.jo").build();
        ItemRequest request = ItemRequest.builder().id(12).requestor(requestor).created(created).description("need a dollar").build();
        Item firstItem = Item.builder().id(84).name("dollar").description("one dollar").available(true).owner(requestor).request(request).build();
        Item secondItem = Item.builder().id(74).name("euro").description("one euro").available(true).owner(requestor).request(request).build();
        List<ItemDTO> itemDTOS = List.of(ItemMapper.itemToItemDTO(firstItem), ItemMapper.itemToItemDTO(secondItem));

        OutcomeItemRequestWithItemsDTO dto = RequestMapper.itemRequestToOutcomeRequestWithItemsDTO(request, itemDTOS);
        JsonContent<OutcomeItemRequestWithItemsDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").isNotEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(firstItem.getId());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(firstItem.getRequest().getId());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(firstItem.getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(firstItem.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(secondItem.getId());
        assertThat(result).extractingJsonPathNumberValue("$.items[1].requestId").isEqualTo(secondItem.getRequest().getId());
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo(secondItem.getName());
        assertThat(result).extractingJsonPathStringValue("$.items[1].description").isEqualTo(secondItem.getDescription());
    }
}
