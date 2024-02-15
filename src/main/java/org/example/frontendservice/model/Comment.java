package org.example.frontendservice.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Comment implements EntityWithId<String> {

    @EqualsAndHashCode.Exclude
    private String id;

    private Post post;
    private User user;
    private String content;
    private LocalDateTime createdAt;

    public Comment(String id) {
        this.id = id;
    }

    public Comment(Post post, User user, String content, LocalDateTime createdAt) {
        this.post = post;
        this.user = user;
        this.content = content;
        if (createdAt != null) {
            this.createdAt = createdAt.truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public LocalDateTime getCreatedAt() {
        if (this.createdAt == null) {
            return null;
        }
        return this.createdAt.truncatedTo(ChronoUnit.SECONDS);
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

    public String getTitle() {
        if (getContent() == null || getContent().isBlank()) {
            return "";
        }
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
