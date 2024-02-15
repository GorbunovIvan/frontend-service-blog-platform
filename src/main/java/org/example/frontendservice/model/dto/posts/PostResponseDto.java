package org.example.frontendservice.model.dto.posts;

import lombok.*;
import org.example.frontendservice.model.Post;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class PostResponseDto {

    @EqualsAndHashCode.Exclude
    private Long id;

    private String content;
    private LocalDateTime createdAt;
    private Long userId;

    public PostResponseDto(@NonNull Post post) {

        this.id = post.getId();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();

        if (post.getUser() != null) {
            this.userId = post.getUserId();
        }
    }
}
