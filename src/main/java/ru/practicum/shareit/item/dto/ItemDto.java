package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private int id;
    @NotBlank(message = "Item name can't be blank")
    private String name;
    @NotBlank(message = "Item description can't be blank")
    private String description;
    @NotNull(message = "Item availability can't be null")
    private Boolean available;
}
