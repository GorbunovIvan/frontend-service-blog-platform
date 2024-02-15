package org.example.frontendservice.model.dto.comments;

import lombok.*;
import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class CommentRequestDto {

    private Post post;
    private User user;
    private String content;
    private LocalDateTime createdAt;

    public CommentRequestDto(Post post, User user, String content) {
        this(post, user, content, LocalDateTime.now());
    }

    public CommentRequestDto(Comment comment) {
        this.post = comment.getPost();
        this.user = comment.getUser();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }

    public Long getPostId() {
        if (getPost() != null) {
            return getPost().getId();
        }
        return null;
    }

    public Long getUserId() {
        if (getUser() != null) {
            return getUser().getId();
        }
        return null;
    }

    public Comment toComment() {
        return new Comment(
                getPost(),
                getUser(),
                getContent(),
                getCreatedAt()
        );
    }
}
