package org.example.frontendservice.model.dto.comments;

import jakarta.annotation.Nonnull;
import lombok.*;
import org.example.frontendservice.model.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class CommentResponseDto {

    @EqualsAndHashCode.Exclude
    private String id;

    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public CommentResponseDto(@Nonnull Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.userId = comment.getUserId();
        this.content = comment.getContent();
        if (comment.getCreatedAt() != null) {
            this.createdAt = comment.getCreatedAt().truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
