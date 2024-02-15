package org.example.frontendservice.utils.modelsbinders.comments;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.EntityWithId;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ModelsBinderForCommentsShallow implements ModelsBinderForComments {

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
        return (User) EntityWithId.createEntityWithId(id, User.class);
    }

    private Post postById(Long id) {
        return (Post) EntityWithId.createEntityWithId(id, Post.class);
    }
}
