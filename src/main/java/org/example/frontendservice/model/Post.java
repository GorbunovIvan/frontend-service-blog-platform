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
public class Post implements EntityWithId<Long> {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String content;
    private LocalDateTime createdAt;
    private User user;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public Post(Long id) {
        this.id = id;
    }

    public Post(String content, LocalDateTime createdAt, User user) {
        this(null, content, createdAt, user);
    }

    public Post(Long id, String content, LocalDateTime createdAt, User user) {
        setId(id);
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

    public Long getUserId() {
        if (getUser() != null) {
            return getUser().getId();
        }
        return null;
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
