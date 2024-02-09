package org.example.frontendservice.service;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.CommentRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CommentServiceDummy implements CommentService {

    private final List<Comment> comments = new ArrayList<>();

    @Override
    public Comment getById(String id) {
        return comments.stream()
                .filter(comment -> comment.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<Comment> getAllByPost(Post post) {
        return comments.stream()
                .filter(comment -> comment.getPost().equals(post))
                .toList();
    }

    @Override
    public List<Comment> getAllByPostId(Long postId) {
        return comments.stream()
                .filter(comment -> comment.getPost().getId().equals(postId))
                .toList();
    }

    public List<Comment> getAllByUser(User user) {
        return comments.stream()
                .filter(comment -> comment.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Comment> getAllByUserId(Long userId) {
        return comments.stream()
                .filter(comment -> comment.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public Comment create(CommentRequestDto comment) {
        var newId = generateNextId();

        var commentToPersist = comment.toComment();
        commentToPersist.setId(newId);
        if (commentToPersist.getCreatedAt() == null) {
            commentToPersist.setCreatedAt(LocalDateTime.now());
        }

        comments.add(commentToPersist);

        log.info("New comment was added - {}", commentToPersist);

        return commentToPersist;
    }

    @Override
    public void deleteById(String id) {
        var comment = getById(id);
        if (comment != null) {
            comments.remove(comment);
        }
    }

    private String generateNextId() {
        return UUID.randomUUID().toString();
    }
}
