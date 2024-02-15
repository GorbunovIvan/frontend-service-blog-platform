package org.example.frontendservice.service.posts;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class PostServiceDummy implements PostService {

    @Override
    public Post getById(Long id) {
        log.error("Unable to get post");
        return null;
    }

    @Override
    public List<Post> getAll() {
        log.error("Unable to get posts");
        return Collections.emptyList();
    }

    @Override
    public List<Post> getAllByUserId(Long userId) {
        log.error("Unable to get posts");
        return Collections.emptyList();
    }

    @Override
    public Post create(PostRequestDto post) {
        var errorMessage = "Unable to create post";
        log.error(errorMessage);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @Override
    public void deleteById(Long id) {
        var errorMessage = "Unable to delete post";
        log.error(errorMessage);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
