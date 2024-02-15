package org.example.frontendservice.utils.modelsbinders.posts;

import org.example.frontendservice.model.EntityWithId;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ModelsBinderForPostsShallow implements ModelsBinderForPosts {

    @Override
    public Post toPost(PostResponseDto postResponse) {
        if (postResponse == null) {
            return null;
        }
        return new Post(
                postResponse.getId(),
                postResponse.getContent(),
                postResponse.getCreatedAt(),
                userById(postResponse.getUserId())
        );
    }

    private User userById(Long id) {
        return (User) EntityWithId.createEntityWithId(id, User.class);
    }
}
