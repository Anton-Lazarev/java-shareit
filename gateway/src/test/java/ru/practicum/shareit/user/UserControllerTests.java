package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private UserClient client;

    @Autowired
    public UserControllerTests(ObjectMapper objectMapper, MockMvc mvc) {
        this.objectMapper = objectMapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void create_whenNameIsNull() {
        UserDTO userDTO = UserDTO.builder().id(1).name(null).email("1asd@mail.ru").build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(client, never()).addUser(userDTO);
    }

    @SneakyThrows
    @Test
    void create_whenEmailIsNull() {
        UserDTO userDTO = UserDTO.builder().id(1).name("first").email(null).build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(client, never()).addUser(userDTO);
    }

    @SneakyThrows
    @Test
    void create_whenIncorrectEmail() {
        UserDTO userDTO = UserDTO.builder().id(1).name("first").email("kgsdgn").build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(client, never()).addUser(userDTO);
    }
}
