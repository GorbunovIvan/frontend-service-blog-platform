package org.example.frontendservice.utils.modelsbinders.posts;

import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.frontendservice.service.comments.CommentService;
import org.example.frontendservice.service.users.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ModelsBinderForPostsImpl implements ModelsBinderForPosts {

    private final UserService userService;
    private final CommentService commentService;

    public ModelsBinderForPostsImpl(@Qualifier("userServiceShallow") UserService userService,
                                    @Qualifier("commentServiceShallow") CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    @Override
    public Post toPost(PostResponseDto postResponse) {
        if (postResponse == null) {
            return null;
        }
        return new Post(
                postResponse.getId(),
                postResponse.getContent(),
                postResponse.getCreatedAt(),
                userById(postResponse.getUserId())
        );
    }

    public Post addDependencyFieldsToPost(@Nonnull Post post) {
        addCommentsToPost(post);
        return post;
    }

    private void addCommentsToPost(@Nonnull Post post) {
        var comments = commentService.getAllByPostId(post.getId());
        if (comments != null) {
            post.setComments(comments);
            for (var comment : post.getComments()) {
                comment.setPost(post);
                if (comment.getUserId() != null) {
                    var userOfComment = userService.getById(comment.getUserId());
                    comment.setUser(userOfComment);
                }
            }
        }
    }

    private User userById(Long id) {
        return userService.getById(id);
    }
}
