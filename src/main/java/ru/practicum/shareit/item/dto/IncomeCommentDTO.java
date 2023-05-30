package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeCommentDTO {
    @NotBlank
    private String text;
    private LocalDateTime created;
}
