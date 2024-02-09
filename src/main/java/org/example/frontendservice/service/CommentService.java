package org.example.frontendservice.service;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.dto.CommentRequestDto;

import java.util.List;

public interface CommentService {
    Comment getById(String id);
    List<Comment> getAllByPostId(Long postId);
    List<Comment> getAllByUserId(Long userId);
    Comment create(CommentRequestDto comment);
    void deleteById(String id);
}
