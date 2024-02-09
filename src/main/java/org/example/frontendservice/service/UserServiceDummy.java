package org.example.frontendservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.CommentRequestDto;
import org.example.frontendservice.model.dto.PostRequestDto;
import org.example.frontendservice.model.dto.UserRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceDummy implements UserService {

    private final List<User> users = new ArrayList<>();

    private final PostService postService;
    private final CommentService commentService;

    @PostConstruct
    private void init() {

        create(new UserRequestDto("username_1", "password_1", LocalDate.now(), "+134234321"));
        create(new UserRequestDto("username_2", "password_2", LocalDate.now(), "+286786782"));
        create(new UserRequestDto("username_3", "password_3", LocalDate.now(), "+372345343"));
        create(new UserRequestDto("username_4", "password_4", LocalDate.now(), "+415645654"));
        create(new UserRequestDto("username_5", "password_5", LocalDate.now(), "+534634645"));

        postService.create(new PostRequestDto("post 1 content", this.getById(2L)));
        postService.create(new PostRequestDto("post 2 content", this.getById(2L)));
        postService.create(new PostRequestDto("post 3 content", this.getById(3L)));
        postService.create(new PostRequestDto("post 4 content", this.getById(1L)));
        postService.create(new PostRequestDto("post 5 content", this.getById(3L)));
        postService.create(new PostRequestDto("post 6 content", this.getById(3L)));
        postService.create(new PostRequestDto("post 7 content", this.getById(1L)));
        postService.create(new PostRequestDto("post 8 content", this.getById(5L)));

        commentService.create(new CommentRequestDto(postService.getById(1L), this.getById(1L), "comment 1 content"));
        commentService.create(new CommentRequestDto(postService.getById(1L), this.getById(3L), "comment 2 content"));
        commentService.create(new CommentRequestDto(postService.getById(2L), this.getById(2L), "comment 3 content"));
        commentService.create(new CommentRequestDto(postService.getById(3L), this.getById(4L), "comment 4 content"));
        commentService.create(new CommentRequestDto(postService.getById(4L), this.getById(4L), "comment 5 content"));
        commentService.create(new CommentRequestDto(postService.getById(4L), this.getById(4L), "comment 6 content"));
        commentService.create(new CommentRequestDto(postService.getById(4L), this.getById(1L), "comment 7 content"));
        commentService.create(new CommentRequestDto(postService.getById(5L), this.getById(2L), "comment 8 content"));
        commentService.create(new CommentRequestDto(postService.getById(6L), this.getById(3L), "comment 9 content"));
        commentService.create(new CommentRequestDto(postService.getById(6L), this.getById(3L), "comment 10 content"));
        commentService.create(new CommentRequestDto(postService.getById(6L), this.getById(3L), "comment 11 content"));

        for (var post : postService.getAll()) {
            post.getUser().addPost(post);
        }

        for (var post : postService.getAll()) {
            var comments = commentService.getAllByPostId(post.getId());
            for (var comment : comments) {
                post.addComment(comment);
            }
        }

        for (var user : this.getAll()) {
            var comments = commentService.getAllByUserId(user.getId());
            for (var comment : comments) {
                user.addComment(comment);
            }
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }

    @Override
    public User getById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public User getByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    @Override
    public User create(UserRequestDto user) {

        var newId = generateNextId();

        if (getByUsername(user.getUsername()) != null) {
            var errorMessage = String.format("User with username '%s' already exists", user.getUsername());
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        var userToPersist = user.toUser();
        userToPersist.setId(newId);
        users.add(userToPersist);

        log.info("New user was registered - {}", userToPersist);

        return userToPersist;
    }

    @Override
    public User update(Long id, UserRequestDto user) {

        var userPersisted = getById(id);
        if (userPersisted == null) {
            var errorMessage = String.format("User with id '%d' was not found", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        var userByUsername = getByUsername(user.getUsername());
        if (userByUsername != null && !userByUsername.getId().equals(id)) {
            var errorMessage = String.format("User with username '%s' already exists", user.getUsername());
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        userPersisted.setUsername(user.getUsername());
        userPersisted.setPassword(user.getPassword()); // without encoding
        userPersisted.setBirthDate(user.getBirthDate());
        userPersisted.setPhoneNumber(user.getPhoneNumber());

        return userPersisted;
    }

    @Override
    public void deleteById(Long id) {
        var user = getById(id);
        if (user != null) {
            users.remove(user);
        }
    }

    private long generateNextId() {

        var maxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);

        return maxId + 1;
    }
}
