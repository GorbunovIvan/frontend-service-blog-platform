package org.example.frontendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.service.users.UserService;
import org.example.frontendservice.utils.UsersUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;
    private final UsersUtil usersUtil;

    @GetMapping
    public String getAll(Model model) {
        var users = userService.getAll();
        model.addAttribute("users", users);
        return "users/users";
    }

    @GetMapping("/self")
    public String getSelfPage() {
        var currentUser = getCurrentUser();
        if (currentUser != null) {
            return "redirect:/users/" + currentUser.getId();
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable Long id) {

        var currentUser = getCurrentUser();

        var user = userService.getById(id);
        if (user == null) {
            var errorMessage = String.format("User with id '%d' was not found", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        model.addAttribute("user", user);
        model.addAttribute("isOwnPage", user.equals(currentUser));
        model.addAttribute("newPost", new PostRequestDto());
        return "users/user";
    }

    @GetMapping("/edit")
    public String edit() {
        var currentUser = getCurrentUser();
        if (currentUser != null) {
            return String.format("redirect:/users/%d/edit", currentUser.getId());
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable Long id) {

        var currentUser = getCurrentUser();

        var user = userService.getById(id);
        if (user == null) {
            var errorMessage = String.format("User with id '%d' was not found", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        if (!user.equals(currentUser)) {
            var errorMessage = "You are not authorized";
            if (currentUser != null) {
                errorMessage = "You are trying to edit another user's page";
            }
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        var userRequestDto = new UserRequestDto(user);
        model.addAttribute("user", userRequestDto);
        model.addAttribute("id", id);
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(Model model, @PathVariable Long id, UserRequestDto user) {

        var currentUser = getCurrentUser();

        var userById = userService.getById(id);
        if (userById == null) {
            var errorMessage = String.format("User with id '%d' was not found", id);
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
        if (!userById.equals(currentUser)) {
            var errorMessage = "You are not authorized";
            if (currentUser != null) {
                errorMessage = "You are trying to edit another user's page";
            }
            log.warn(errorMessage);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        userService.update(id, user);

        model.addAttribute("user", user);
        return "redirect:/users/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        var currentUser = getCurrentUser();

        if (currentUser != null) {

            if (!currentUser.getId().equals(id)) {
                var errorMessage = "You are trying to delete another user's page";
                log.warn(errorMessage);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }

            userService.deleteById(id);
            usersUtil.logout();
        }

        return "redirect:/users";
    }

    @ModelAttribute("currentUser")
    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
