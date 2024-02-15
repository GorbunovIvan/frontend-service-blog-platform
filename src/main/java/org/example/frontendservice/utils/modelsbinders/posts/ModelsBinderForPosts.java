package org.example.frontendservice.utils.modelsbinders.posts;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.dto.posts.PostResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public interface ModelsBinderForPosts {

    Post toPost(PostResponseDto postResponse);

    default List<Post> toPosts(List<PostResponseDto> posts) {
        return posts.stream()
                .map(this::toPost)
                .collect(Collectors.toList());
    }
}
