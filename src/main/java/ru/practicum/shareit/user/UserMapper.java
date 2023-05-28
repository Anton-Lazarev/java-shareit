package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.ShortUser;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto userToUserDTO(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User userDtoToUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static ShortUser userToShortUser(User user) {
        return ShortUser.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
