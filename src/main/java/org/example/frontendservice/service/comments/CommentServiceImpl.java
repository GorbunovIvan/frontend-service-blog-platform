package org.example.frontendservice.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.example.frontendservice.utils.modelsbinders.comments.ModelsBinderForComments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@ConditionalOnBean(name = "commentServiceDetails")
@Primary
@RequiredArgsConstructor
@Log4j2
public class CommentServiceImpl implements CommentService {

    private final CommentServiceDetails commentServiceDetails;
    private final CommentServiceDummy commentServiceDummy;

    private final ModelsBinderForComments modelsBinder;

    @Override
    public Comment getById(String id) {

        CommentResponseDto comment;

        try {
            comment = commentServiceDetails.getById(id);
        } catch (Exception e) {
            return commentServiceDummy.getById(id);
        }

        if (comment == null) {
            return null;
        }

        return modelsBinder.toComment(comment);
    }

    @Override
    public List<Comment> getAllByPostId(Long postId) {

        List<CommentResponseDto> comments;

        try {
            comments = commentServiceDetails.getAllByPostId(postId);
        } catch (Exception e) {
            return commentServiceDummy.getAllByPostId(postId);
        }

        return modelsBinder.toComments(comments);
    }

    @Override
    public List<Comment> getAllByUserId(Long userId) {

        List<CommentResponseDto> comments;

        try {
            comments = commentServiceDetails.getAllByUserId(userId);
        } catch (Exception e) {
            return commentServiceDummy.getAllByUserId(userId);
        }

        return modelsBinder.toComments(comments);
    }

    @Override
    public Comment create(CommentRequestDto comment) {

        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        CommentResponseDto commentCreated;

        try {
            commentCreated = commentServiceDetails.create(comment);
        } catch (Exception e) {
            return commentServiceDummy.create(comment);
        }

        if (commentCreated == null) {
            return null;
        }

        return modelsBinder.toComment(commentCreated);
    }

    @Override
    public void deleteById(String id) {

        try {
            commentServiceDetails.deleteById(id);
        } catch (Exception e) {
            commentServiceDummy.deleteById(id);
        }
    }
}
