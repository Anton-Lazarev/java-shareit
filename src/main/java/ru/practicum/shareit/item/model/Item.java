package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private int id;
    @NotBlank(message = "Item name can't be blank")
    private String name;
    @NotBlank(message = "Item description can't be blank")
    private String description;
    @NotNull(message = "Item availability can't be null")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
