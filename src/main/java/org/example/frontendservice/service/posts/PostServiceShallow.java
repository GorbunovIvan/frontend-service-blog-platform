package org.example.frontendservice.service.posts;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.frontendservice.utils.modelsbinders.posts.ModelsBinderForPostsShallow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(name = "postServiceDetails")
@RequiredArgsConstructor
@Log4j2
public class PostServiceShallow implements PostService {

    private final PostServiceDetails postServiceDetails;
    private final PostServiceDummy postServiceDummy;

    private final ModelsBinderForPostsShallow modelsBinder;

    @Override
    public Post getById(Long id) {

        PostResponseDto postResponseDto;

        try {
            postResponseDto = postServiceDetails.getById(id);
        } catch (StatusRuntimeException e) {
            return postServiceDummy.getById(id);
        }

        if (postResponseDto == null || isPostEmpty(postResponseDto)) {
            return null;
        }

        return modelsBinder.toPost(postResponseDto);
    }

    @Override
    public List<Post> getAll() {

        List<PostResponseDto> postsResponsesDto;

        try {
            postsResponsesDto = postServiceDetails.getAll();
        } catch (StatusRuntimeException e) {
            return postServiceDummy.getAll();
        }

        return modelsBinder.toPosts(postsResponsesDto);
    }

    @Override
    public List<Post> getAllByUserId(Long userId) {

        List<PostResponseDto> postsResponsesDto;

        try {
            postsResponsesDto = postServiceDetails.getAllByUserId(userId);
        } catch (StatusRuntimeException e) {
            return postServiceDummy.getAllByUserId(userId);
        }

        return modelsBinder.toPosts(postsResponsesDto);
    }

    @Override
    public Post create(PostRequestDto post) {

        PostResponseDto postResponseDto;

        try {
            postResponseDto = postServiceDetails.create(post);
        } catch (StatusRuntimeException e) {
            return postServiceDummy.create(post);
        }

        if (postResponseDto == null) {
            return null;
        }

        return modelsBinder.toPost(postResponseDto);
    }

    @Override
    public void deleteById(Long id) {

        try {
            postServiceDetails.deleteById(id);
        } catch (StatusRuntimeException e) {
            postServiceDummy.deleteById(id);
        }
    }

    private boolean isPostEmpty(PostResponseDto post) {
        if (post == null) {
            return true;
        }
        return (post.getId() == null || post.getId() == 0)
                && Optional.ofNullable(post.getContent()).orElse("").isEmpty();
    }
}
