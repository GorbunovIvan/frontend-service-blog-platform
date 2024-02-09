package org.example.frontendservice.service;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.PostRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class PostServiceDummy implements PostService {

    private final List<Post> posts = new ArrayList<>();

    @Override
    public Post getById(Long id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(posts);
    }

    public List<Post> getAllByUser(User user) {
        return posts.stream()
                .filter(post -> post.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Post> getAllByUserId(Long userId) {
        return posts.stream()
                .filter(post -> post.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public Post create(PostRequestDto post) {

        var newId = generateNextId();

        var postToPersist = post.toPost();
        postToPersist.setId(newId);
        if (postToPersist.getCreatedAt() == null) {
            postToPersist.setCreatedAt(LocalDateTime.now());
        }
        
        posts.add(postToPersist);

        log.info("New post was created - {}", postToPersist);

        return postToPersist;
    }

    @Override
    public void deleteById(Long id) {
        var post = getById(id);
        if (post != null) {
            posts.remove(post);
        }
    }

    private long generateNextId() {

        var maxId = posts.stream()
                .mapToLong(Post::getId)
                .max()
                .orElse(0);

        return maxId + 1;
    }
}
