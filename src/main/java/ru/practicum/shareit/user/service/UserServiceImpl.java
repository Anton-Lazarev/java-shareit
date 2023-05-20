package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserDto> getAllUsers() {
        ArrayList<UserDto> usersDTO = new ArrayList<>();
        for (User user : repository.findAll()) {
            usersDTO.add(UserMapper.userToUserDTO(user));
        }
        log.info("Get usersDTO list with size {}", usersDTO.size());
        return usersDTO;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User newUser = repository.save(UserMapper.userDtoToUser(userDto));
        log.info("Создан пользователь с ID {} и именем {}", newUser.getId(), newUser.getName());
        return UserMapper.userToUserDTO(newUser);
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        if (!repository.existsById(userDto.getId())) {
            throw new UserNotFoundException("User with ID " + userDto.getId() + " not present");
        }
        User user = repository.findById(userDto.getId()).get();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(user.getEmail()) &&
                    repository.findByEmailIgnoreCase(userDto.getEmail()).size() > 0) {
                throw new EmailAlreadyExistException("Email " + userDto.getEmail() + " already exist in base, can't patch user");
            }
            user.setEmail(userDto.getEmail());
        }
        log.info("User with ID {} updated", userDto.getId());
        return UserMapper.userToUserDTO(repository.save(user));
    }

    @Override
    public void deleteUser(int id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Deleting user with ID {}", id);
        repository.deleteById(id);
    }

    @Override
    public UserDto getUserByID(int id) {
        Optional<User> user= repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Getting user with ID {}", id);
        return UserMapper.userToUserDTO(user.get());
    }
}
