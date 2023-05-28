package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment incomeCommentDtoToComment(IncomeCommentDTO dto) {
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static OutcomeCommentDTO commentToOutcomeCommentDTO(Comment comment) {
        return OutcomeCommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
