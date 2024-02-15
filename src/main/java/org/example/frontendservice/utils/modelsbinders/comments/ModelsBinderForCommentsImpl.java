package org.example.frontendservice.utils.modelsbinders.comments;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.example.frontendservice.service.posts.PostService;
import org.example.frontendservice.service.users.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ModelsBinderForCommentsImpl implements ModelsBinderForComments {

    private final UserService userService;
    private final PostService postService;

    public ModelsBinderForCommentsImpl(@Qualifier("userServiceShallow") UserService userService,
                                       @Qualifier("postServiceShallow") PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @Override
    public Comment toComment(CommentResponseDto commentResponse) {
        if (commentResponse == null) {
            return null;
        }
        return new Comment(
                commentResponse.getId(),
                postById(commentResponse.getPostId()),
                userById(commentResponse.getUserId()),
                commentResponse.getContent(),
                commentResponse.getCreatedAt()
        );
    }

    private User userById(Long id) {
        return userService.getById(id);
    }

    private Post postById(Long id) {
        return postService.getById(id);
    }
}
