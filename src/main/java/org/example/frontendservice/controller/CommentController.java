package org.example.frontendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.service.comments.CommentService;
import org.example.frontendservice.utils.UsersUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
@Log4j2
public class CommentController {

    private final CommentService commentService;

    private final UsersUtil usersUtil;

    @PostMapping
    public String create(CommentRequestDto comment) {
        commentService.create(comment);
        return "redirect:/posts/" + comment.getPostId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {

        var comment = commentService.getById(id);

        if (comment != null) {
            var currentUser = getCurrentUser();
            if (!comment.getUser().equals(currentUser)) {
                var errorMessage = "You are trying to delete another user's comment";
                log.warn(errorMessage);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
            commentService.deleteById(id);
        }

        return "redirect:/users/self";
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
