package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Collection;

public interface UserService {
    Collection<UserDTO> getAllUsers();

    UserDTO addUser(UserDTO userDto);

    UserDTO patchUser(UserDTO userDto);

    void deleteUser(int id);

    UserDTO getUserByID(int id);
}
