package org.example.frontendservice.service.comments;

import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;

import java.util.List;

public interface CommentService {

    Comment getById(String id);
    List<Comment> getAllByPostId(Long postId);
    List<Comment> getAllByUserId(Long userId);
    Comment create(CommentRequestDto comment);
    void deleteById(String id);

    default List<Comment> getAllByPost(@Nonnull Post post) {
        var comments = getAllByPostId(post.getId());
        if (post.getContent() != null) {
            comments.forEach(comment -> comment.setPost(post));
        }
        return comments;
    }

    default List<Comment> getAllByUser(@Nonnull User user) {
        var comments = getAllByUserId(user.getId());
        if (user.getUsername() != null) {
            comments.forEach(comment -> comment.setUser(user));
        }
        return comments;
    }
}
