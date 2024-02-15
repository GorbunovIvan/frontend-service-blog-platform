package org.example.frontendservice.service.comments;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class CommentServiceDummy implements CommentService {

    @Override
    public Comment getById(String id) {
        log.error("Unable to get comment");
        return null;
    }

    @Override
    public List<Comment> getAllByPostId(Long postId) {
        log.error("Unable to get comments");
        return Collections.emptyList();
    }

    @Override
    public List<Comment> getAllByUserId(Long userId) {
        log.error("Unable to get comments");
        return Collections.emptyList();
    }

    @Override
    public Comment create(CommentRequestDto comment) {
        var errorMessage = "Unable to create comment";
        log.error(errorMessage);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @Override
    public void deleteById(String id) {
        var errorMessage = "Unable to create comment";
        log.error(errorMessage);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
