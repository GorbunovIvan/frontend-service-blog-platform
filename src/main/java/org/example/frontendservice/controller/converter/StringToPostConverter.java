package org.example.frontendservice.controller.converter;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.service.PostService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToPostConverter implements Converter<String, Post> {

    private final PostService postService;

    @Override
    public Post convert(@Nonnull String source) {

        if (!source.startsWith("Post(id=")) {
            return null;
        }
        var startOfId = 8; // after "Post(id="
        var endOfId = source.indexOf(",");
        String idAsString = source.substring(startOfId, endOfId);

        var id = Long.parseLong(idAsString);

        return postService.getById(id);
    }
}
