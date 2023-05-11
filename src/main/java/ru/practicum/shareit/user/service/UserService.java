package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto patchUser(UserDto userDto);

    void deleteUser(int id);

    UserDto getUserByID(int id);
}
