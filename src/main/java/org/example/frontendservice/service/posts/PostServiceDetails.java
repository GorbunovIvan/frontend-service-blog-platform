package org.example.frontendservice.service.posts;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.frontendservice.utils.PostsUtils;
import org.example.postservice.grpc.PostServiceGrpc;
import org.example.postservice.grpc.PostServiceOuterClass.EmptyBody;
import org.example.postservice.grpc.PostServiceOuterClass.IdRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@ConditionalOnBean(name = "blockingStub")
@RequiredArgsConstructor
@Log4j2
public class PostServiceDetails {

    private final PostServiceGrpc.PostServiceBlockingStub blockingStub;

    private final PostsUtils postsUtils;

    public PostResponseDto getById(Long id) {

        var request = IdRequest.newBuilder()
                .setId(id)
                .build();

        var post = blockingStub.getById(request);
        return postsUtils.toPostResponseDto(post);
    }
    
    public List<PostResponseDto> getAll() {

        var request = EmptyBody.newBuilder().build();

        var postsResponse = blockingStub.getAll(request).getPostsList();
        return postsUtils.toPostsResponseDto(postsResponse);
    }

    public List<PostResponseDto> getAllByUserId(Long userId) {

        var request = IdRequest.newBuilder()
            .setId(userId)
            .build();

        var postsResponse = blockingStub.getAllByUserId(request).getPostsList();
        return postsUtils.toPostsResponseDto(postsResponse);
    }

    public PostResponseDto create(PostRequestDto post) {

        var request = postsUtils.toPostRequest(post);

        var postResponse = blockingStub.create(request);
        var postCreated = postsUtils.toPostResponseDto(postResponse);

        log.info("New post was created - {}", postCreated);

        return postCreated;
    }
    
    public void deleteById(Long id) {

        var request = IdRequest.newBuilder()
            .setId(id)
            .build();

        var response = blockingStub.deleteById(request);

        if (response == null) {
            var errorMessage = String.format("Something wrong with deleting post by id '%d'", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }
}
