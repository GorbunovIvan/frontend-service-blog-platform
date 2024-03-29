package org.example.frontendservice.utils.modelsbinders.comments;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface ModelsBinderForComments {

    Comment toComment(CommentResponseDto commentResponse);

    default List<Comment> toComments(List<CommentResponseDto> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(this::toComment)
                .collect(Collectors.toList());
    }
}
