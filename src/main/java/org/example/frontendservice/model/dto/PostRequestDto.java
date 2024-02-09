package org.example.frontendservice.model.dto;

import lombok.*;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class PostRequestDto {

    private String content;
    private LocalDateTime createdAt;
    private User user;

    public PostRequestDto(String content, User user) {
        this(content, LocalDateTime.now(), user);
    }

    public PostRequestDto(Post post) {
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.user = post.getUser();
    }

    public Post toPost() {
        return new Post(
                getContent(),
                getCreatedAt(),
                getUser()
        );
    }
}
