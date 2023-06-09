package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> dtos = repository.findAll().stream().map(UserMapper::userToUserDTO).collect(Collectors.toList());
        log.info("Get usersDTO list with size {}", dtos.size());
        return dtos;
    }

    @Override
    @Transactional
    public UserDTO addUser(UserDTO userDto) {
        User newUser = repository.save(UserMapper.userDtoToUser(userDto));
        log.info("Создан пользователь с ID {} и именем {}", newUser.getId(), newUser.getName());
        return UserMapper.userToUserDTO(newUser);
    }

    @Override
    @Transactional
    public UserDTO patchUser(UserDTO userDto) {
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
    @Transactional
    public void deleteUser(int id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Deleting user with ID {}", id);
        repository.deleteById(id);
    }

    @Override
    public UserDTO getUserByID(int id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with ID " + id + " not present");
        }
        log.info("Getting user with ID {}", id);
        return UserMapper.userToUserDTO(user.get());
    }
}
