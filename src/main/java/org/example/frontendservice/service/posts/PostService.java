package org.example.frontendservice.service.posts;

import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;

import java.util.List;

public interface PostService {

    Post getById(Long id);
    List<Post> getAll();
    List<Post> getAllByUserId(Long userId);
    Post create(PostRequestDto post);
    void deleteById(Long id);

    default List<Post> getAllByUser(@Nonnull User user) {
        var posts = getAllByUserId(user.getId());
        if (user.getUsername() != null) {
            posts.forEach(comment -> comment.setUser(user));
        }
        return posts;
    }
}
