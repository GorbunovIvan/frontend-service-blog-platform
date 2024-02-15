package org.example.frontendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.service.posts.PostService;
import org.example.frontendservice.utils.UsersUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;

    private final UsersUtil usersUtil;

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable Long id) {

        var currentUser = getCurrentUser();

        var post = postService.getById(id);
        if (post == null) {
            var errorMessage = String.format("Post with id '%d' was not found", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        model.addAttribute("post", post);
        model.addAttribute("isOwnPost", post.getUser().equals(currentUser));
        model.addAttribute("newComment", new CommentRequestDto(post, getCurrentUser(), ""));
        return "posts/post";
    }

    @GetMapping
    public String getAll(Model model) {
        var posts = postService.getAll();
        model.addAttribute("posts", posts);
        return "posts/posts";
    }

    @PostMapping
    public String create(PostRequestDto post) {
        postService.create(post);
        return "redirect:/users/self";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        var post = postService.getById(id);

        if (post != null) {
            var currentUser = getCurrentUser();
            if (!post.getUser().equals(currentUser)) {
                var errorMessage = "You are trying to delete another user's post";
                log.warn(errorMessage);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
            postService.deleteById(id);
        }

        return "redirect:/users/self";
    }

    @ModelAttribute("currentUser")
    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
