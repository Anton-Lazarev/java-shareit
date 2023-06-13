package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Server : GET to /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserByID(@PathVariable int id) {
        log.info("Server : GET to /users/{}", id);
        return userService.getUserByID(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDto) {
        log.info("Server : POST to /users with {}", userDto.toString());
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDTO patch(@PathVariable int id, @RequestBody UserDTO userDto) {
        userDto.setId(id);
        log.info("Server : PATCH to /users/{} with {}", id, userDto.toString());
        return userService.patchUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Server : DELETE to /users/{}", id);
        userService.deleteUser(id);
    }
}
