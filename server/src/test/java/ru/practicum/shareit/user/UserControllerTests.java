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
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private UserService userService;

    @Autowired
    public UserControllerTests(ObjectMapper objectMapper, MockMvc mvc) {
        this.objectMapper = objectMapper;
        this.mvc = mvc;
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersNotCreated() {
        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        verify(userService).getAllUsers();
        assertEquals("[]", response);
    }

    @SneakyThrows
    @Test
    void create_correctUserCreation() {
        UserDTO userDTO = UserDTO.builder().name("first").email("f@f.ru").build();
        UserDTO userWithIdDTO = UserDTO.builder().id(1).name("first").email("f@f.ru").build();
        when(userService.addUser(userDTO)).thenReturn(userWithIdDTO);

        String response = mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).addUser(userDTO);
        assertEquals(objectMapper.writeValueAsString(userWithIdDTO), response);
    }

    @SneakyThrows
    @Test
    void patch_correctOnlyWithName() {
        UserDTO patchDTO = UserDTO.builder().id(1).name("patch").build();
        UserDTO patchedUserDTO = UserDTO.builder().id(1).name("patch").email("f@f.ru").build();

        when(userService.patchUser(patchDTO)).thenReturn(patchedUserDTO);
        String response = mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(patchDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).patchUser(patchDTO);
        assertEquals(objectMapper.writeValueAsString(patchedUserDTO), response);
    }

    @SneakyThrows
    @Test
    void patch_correctOnlyWithMail() {
        UserDTO patchDTO = UserDTO.builder().id(1).email("s@s.ru").build();
        UserDTO patchedUserDTO = UserDTO.builder().id(1).name("first").email("s@s.ru").build();

        when(userService.patchUser(patchDTO)).thenReturn(patchedUserDTO);
        String response = mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(patchDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).patchUser(patchDTO);
        assertEquals(objectMapper.writeValueAsString(patchedUserDTO), response);
    }

    @SneakyThrows
    @Test
    void patch_correctWithMailAndName() {
        UserDTO patchDTO = UserDTO.builder().id(1).name("patch").email("s@s.ru").build();
        UserDTO patchedUserDTO = UserDTO.builder().id(1).name("patch").email("s@s.ru").build();

        when(userService.patchUser(patchDTO)).thenReturn(patchedUserDTO);
        String response = mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(patchDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).patchUser(patchDTO);
        assertEquals(objectMapper.writeValueAsString(patchedUserDTO), response);
    }

    @SneakyThrows
    @Test
    void getByID_correctGetting() {
        UserDTO userDTO = UserDTO.builder().id(1).name("first").email("f@f.ru").build();

        when(userService.getUserByID(anyInt())).thenReturn(userDTO);
        String response = mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).getUserByID(1);
        assertEquals(objectMapper.writeValueAsString(userDTO), response);
    }

    @SneakyThrows
    @Test
    void delete_correctDeleting() {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(userService).deleteUser(1);
    }
}
