package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.ShortUser;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ShortUserJsonTests {
    @Autowired
    private JacksonTester<ShortUser> json;

    @SneakyThrows
    @Test
    void correct_transformShortUserToJSON() {
        User user = User.builder().id(8).name("Jo").email("j@i.jo").build();
        ShortUser shortUser = UserMapper.userToShortUser(user);

        JsonContent<ShortUser> result = json.write(shortUser);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(shortUser.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(shortUser.getName());
    }
}
