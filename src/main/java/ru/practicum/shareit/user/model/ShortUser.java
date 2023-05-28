package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUser {
    private int id;
    private String name;
}
