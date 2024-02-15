package org.example.frontendservice.utils.modelsbinders.users;

import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.User;
import org.example.frontendservice.service.comments.CommentService;
import org.example.frontendservice.service.posts.PostService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class ModelsBinderForUsersImpl implements ModelsBinderForUsers {

    private final PostService postService;
    private final CommentService commentService;

    public ModelsBinderForUsersImpl(@Qualifier("postServiceShallow") PostService postService,
                                    @Qualifier("commentServiceShallow") CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    public User addDependencyFieldsToUser(@Nonnull User user) {
        addPostsToUser(user);
        addCommentsToUser(user);
        return user;
    }

    public List<User> addDependencyFieldsToUsers(List<User> users) {
        for (var user : users) {
            addPostsToUser(user);
            addCommentsToUser(user);
        }
        return users;
    }

    private void addPostsToUser(@Nonnull User user) {
        var posts = postService.getAllByUser(user);
        if (posts != null) {
            user.setPosts(posts);
            for (var post : user.getPosts()) {
                post.setUser(user);
            }
        }
    }

    private void addCommentsToUser(@Nonnull User user) {
        var comments = commentService.getAllByUser(user);
        if (comments != null) {
            user.setComments(comments);
            for (var comment : user.getComments()) {
                comment.setUser(user);
            }
        }
    }
}
