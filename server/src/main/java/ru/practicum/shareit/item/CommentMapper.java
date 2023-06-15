package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.IncomeCommentDTO;
import ru.practicum.shareit.item.dto.OutcomeCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment incomeCommentDtoToComment(IncomeCommentDTO dto, User author, Item item) {
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .author(author)
                .item(item)
                .build();
    }

    public OutcomeCommentDTO commentToOutcomeCommentDTO(Comment comment) {
        return OutcomeCommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
