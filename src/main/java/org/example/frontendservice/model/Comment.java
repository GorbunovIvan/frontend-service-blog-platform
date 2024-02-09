package org.example.frontendservice.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Comment {

    private String id;
    private Post post;
    private User user;
    private String content;
    private LocalDateTime createdAt;

    public Comment(Post post, User user, String content, LocalDateTime createdAt) {
        this.post = post;
        this.user = user;
        this.content = content;
        if (createdAt != null) {
            this.createdAt = createdAt.truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public LocalDateTime getCreatedAt() {
        if (this.createdAt != null) {
            return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
        }
        return this.createdAt;
    }

    public Long getPostId() {
        return getPost().getId();
    }

    public String getTitle() {
        if (getContent().length() <= 100) {
            return getContent();
        }
        var shortenedContent = getContent().substring(0, 97);
        return shortenedContent + "...";
    }

    public String toStringShort() {
        return String.format("%s - %s", getCreatedAt(), getTitle());
    }
}
