package org.example.frontendservice.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Post {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String content;
    private LocalDateTime createdAt;
    private User user;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public Post(String content, LocalDateTime createdAt, User user) {
        this.content = content;
        this.user = user;
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

    public String getTitle() {
        if (getContent().length() <= 100) {
            return getContent();
        }
        var shortenedContent = getContent().substring(0, 97);
        return shortenedContent + "...";
    }

    public void addComment(Comment comment) {
        getComments().add(comment);
    }

    public String toStringShort() {
        return String.format("%s - %s", getCreatedAt(), getTitle());
    }
}
