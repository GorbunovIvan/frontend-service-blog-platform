package org.example.frontendservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.frontendservice.model.dto.UserRequestDto;
import org.example.frontendservice.service.UserService;
import org.example.frontendservice.utils.UsersUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UsersUtil usersUtil;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRequestDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRequestDto user) {
        usersUtil.logout();
        userService.create(user);
        return "redirect:/auth/login";
    }
}
