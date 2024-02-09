package org.example.frontendservice.utils;

import lombok.RequiredArgsConstructor;
import org.example.frontendservice.model.User;
import org.example.frontendservice.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersUtil {

    private final UserService userService;

    public User getCurrentUser() {

        return userService.getAll().get(0);

//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return null;
//        }
//
//        var principal = authentication.getPrincipal();
//
//        if (principal instanceof User user) {
//            return user;
//        } else if (principal instanceof UserDetails user) {
//            return userService.getByUsername(user.getUsername());
//        }
//
//        return null;
    }

}
