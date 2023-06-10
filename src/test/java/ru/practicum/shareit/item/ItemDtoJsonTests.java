package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTests {
    @Autowired
    private JacksonTester<ItemDTO> json;

    @SneakyThrows
    @Test
    void correct_transformItemDtoToJSON() {
        User user = User.builder().id(41).name("Jo").email("j@i.jo").build();
        Item item = Item.builder().id(31).owner(user).name("dollar").description("one dollar").available(true).build();

        ItemDTO dto = ItemMapper.itemToItemDTO(item);
        JsonContent<ItemDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
    }
}
