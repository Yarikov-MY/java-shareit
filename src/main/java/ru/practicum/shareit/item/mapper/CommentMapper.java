package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        if (comment != null) {
            return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
        } else {
            return null;
        }
    }

    public static Comment toComment(CommentDto commentDto) {
        if (commentDto != null) {
            return new Comment(null, commentDto.getText(), null, null, null);
        } else {
            return null;
        }
    }
}
