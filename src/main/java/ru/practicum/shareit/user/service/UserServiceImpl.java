package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository storage;

    @Override
    public Collection<UserDto> getAllUsers() {
        ArrayList<UserDto> usersDTO = new ArrayList<>();
        for (User user : storage.getAll()) {
            usersDTO.add(UserMapper.userToUserDTO(user));
        }
        log.info("Get usersDTO list with size {}", usersDTO.size());
        return usersDTO;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (storage.containsEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistException("Email " + userDto.getEmail() + " already exist in base, can't add user");
        }
        User newUser = storage.addUser(UserMapper.userDtoToUser(userDto));
        log.info("Создан пользователь с ID {} и именем {}", newUser.getId(), newUser.getName());
        return UserMapper.userToUserDTO(newUser);
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        if (!storage.containsID(userDto.getId())) {
            throw new UserNotFoundException("User with ID " + userDto.getId() + " not present");
        }
        User user = User.builder()
                .id(storage.findUserByID(userDto.getId()).getId())
                .name(storage.findUserByID(userDto.getId()).getName())
                .email(storage.findUserByID(userDto.getId()).getEmail())
                .build();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(user.getEmail()) && storage.containsEmail(userDto.getEmail())) {
                throw new EmailAlreadyExistException("Email " + userDto.getEmail() + " already exist in base, can't patch user");
            }
            user.setEmail(userDto.getEmail());
        }
        log.info("User with ID {} updated", user.getId());
        storage.updateUser(user);
        return UserMapper.userToUserDTO(user);
    }

    @Override
    public void deleteUser(int id) {
        if (!storage.containsID(id)) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Deleting user with ID {}", id);
        storage.deleteUser(id);
    }

    @Override
    public UserDto getUserByID(int id) {
        if (!storage.containsID(id)) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Getting user with ID {}", id);
        return UserMapper.userToUserDTO(storage.findUserByID(id));
    }
}
