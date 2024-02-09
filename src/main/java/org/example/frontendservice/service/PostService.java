package org.example.frontendservice.service;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.dto.PostRequestDto;

import java.util.List;

public interface PostService {
    Post getById(Long id);
    List<Post> getAll();
    List<Post> getAllByUserId(Long userId);
    Post create(PostRequestDto post);
    void deleteById(Long id);
}
