package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.repository.UserRamRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {
    private UserService service;
    UserDto firstUserDTO;
    UserDto secondUserDTO;

    @BeforeEach
    void beforeEach() {
        service = new UserServiceImpl(new UserRamRepository());
        firstUserDTO = UserDto.builder().name("first").email("first@mail.ru").build();
        secondUserDTO = UserDto.builder().name("second").email("second@mail.ru").build();
    }

    @Test
    void emptyUsersWhenNothingAdded() {
        assertEquals(0, service.getAllUsers().size());
    }

    @Test
    void correctAddUserIdIfIncorrectIdInDTO() {
        firstUserDTO.setId(85);
        service.addUser(firstUserDTO);
        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
    }

    @Test
    void correctAddUser() {
        service.addUser(secondUserDTO);
        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
        assertEquals("second", users.get(0).getName());
        assertEquals("second@mail.ru", users.get(0).getEmail());
    }

    @Test
    void getExceptionWhenSameEmailInAddingUser() {
        service.addUser(secondUserDTO);
        firstUserDTO.setEmail("second@mail.ru");
        final EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class, () -> service.addUser(firstUserDTO));
        assertEquals("Email second@mail.ru already exist in base, can't add user", exception.getMessage());
    }

    @Test
    void correctAddFewUsers() {
        service.addUser(secondUserDTO);
        service.addUser(firstUserDTO);
        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(2, users.size());

        assertEquals(1, users.get(0).getId());
        assertEquals("second", users.get(0).getName());
        assertEquals("second@mail.ru", users.get(0).getEmail());

        assertEquals(2, users.get(1).getId());
        assertEquals("first", users.get(1).getName());
        assertEquals("first@mail.ru", users.get(1).getEmail());
    }

    @Test
    void correctFullPatchUser() {
        service.addUser(firstUserDTO);
        UserDto update = UserDto.builder().id(1).name("update").email("update@email.com").build();
        service.patchUser(update);

        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
        assertEquals("update", users.get(0).getName());
        assertEquals("update@email.com", users.get(0).getEmail());
    }

    @Test
    void correctPatchUserWithSameEmail() {
        service.addUser(firstUserDTO);
        UserDto update = UserDto.builder().id(1).name("update").email("first@mail.ru").build();
        service.patchUser(update);

        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
        assertEquals("update", users.get(0).getName());
        assertEquals("first@mail.ru", users.get(0).getEmail());
    }

    @Test
    void correctPatchOnlyEmail() {
        service.addUser(firstUserDTO);
        UserDto update = UserDto.builder().id(1).email("update@email.com").build();
        service.patchUser(update);

        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
        assertEquals("first", users.get(0).getName());
        assertEquals("update@email.com", users.get(0).getEmail());
    }

    @Test
    void correctPatchOnlyName() {
        service.addUser(firstUserDTO);
        UserDto update = UserDto.builder().id(1).name("update").build();
        service.patchUser(update);

        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.get(0).getId());
        assertEquals("update", users.get(0).getName());
        assertEquals("first@mail.ru", users.get(0).getEmail());
    }

    @Test
    void getExceptionIfIdNotExistWhenPatchUser() {
        service.addUser(firstUserDTO);
        UserDto update = UserDto.builder().id(7474).name("update").build();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.patchUser(update));
        assertEquals("User with ID 7474 not present", exception.getMessage());
    }

    @Test
    void getExceptionIfEmailAlreadyExistWhenPatchUser() {
        service.addUser(firstUserDTO);
        service.addUser(secondUserDTO);
        UserDto update = UserDto.builder().id(1).name("update").email("second@mail.ru").build();
        final EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class, () -> service.patchUser(update));
        assertEquals("Email second@mail.ru already exist in base, can't patch user", exception.getMessage());
    }

    @Test
    void correctDeleteUser() {
        service.addUser(secondUserDTO);
        service.addUser(firstUserDTO);
        service.deleteUser(1);

        ArrayList<UserDto> users = new ArrayList<>(service.getAllUsers());
        assertEquals(1, users.size());
        assertEquals(2, users.get(0).getId());
        assertEquals("first", users.get(0).getName());
        assertEquals("first@mail.ru", users.get(0).getEmail());
    }

    @Test
    void getExceptionIfIdNotExistWhenDeleteUser() {
        service.addUser(firstUserDTO);
        service.addUser(secondUserDTO);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.deleteUser(3548));
        assertEquals("User with ID 3548 not present", exception.getMessage());
    }

    @Test
    void correctGettingUserByID() {
        service.addUser(firstUserDTO);
        service.addUser(secondUserDTO);
        UserDto user = service.getUserByID(2);
        assertEquals(2, user.getId());
        assertEquals("second", user.getName());
        assertEquals("second@mail.ru", user.getEmail());
    }

    @Test
    void getExceptionIfIdNotExistWhenGettingUserByID() {
        service.addUser(firstUserDTO);
        service.addUser(secondUserDTO);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.getUserByID(7412));
        assertEquals("User with ID 7412 not present", exception.getMessage());
    }
}
