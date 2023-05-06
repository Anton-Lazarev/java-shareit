package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private int id;
    @NotBlank(message = "User name can't be blank")
    private String name;
    @NotBlank(message = "User email can't be blank")
    @Email(message = "Incorrect email")
    private String email;
}
