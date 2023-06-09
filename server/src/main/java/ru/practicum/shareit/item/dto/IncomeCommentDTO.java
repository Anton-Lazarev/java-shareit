package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncomeCommentDTO {
    private String text;
    private LocalDateTime created;
}
