package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeItemRequestDTO {
    @NotBlank (message = "Description in request can't be blank")
    private String description;
    private LocalDateTime created;
}
