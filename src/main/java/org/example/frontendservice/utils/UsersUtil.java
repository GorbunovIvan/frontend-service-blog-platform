package org.example.frontendservice.utils;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UsersUtil {

    public User getCurrentUser() {
        var authentication = getCurrentAuthentication();
        if (authentication == null) {
            return null;
        }
        var principal = authentication.getPrincipal();
        return getUserFromPrincipal(principal);
    }

    public void updateFieldsOfCurrentUser(User newCurrentUser) {

        var authentication = getCurrentAuthentication();
        if (authentication == null) {
            var errorMessage = "No user is currently authorized";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            if (newCurrentUser.getUsername() != null) {
                user.setUsername(newCurrentUser.getUsername());
            }
            if (newCurrentUser.getPassword() != null) {
                user.setPassword(newCurrentUser.getPassword());
            }
            if (newCurrentUser.getBirthDate() != null) {
                user.setBirthDate(newCurrentUser.getBirthDate());
            }
            if (newCurrentUser.getPhoneNumber() != null) {
                user.setPhoneNumber(newCurrentUser.getPhoneNumber());
            }
        } else {
            var errorMessage = "Failed to change properties of the current user (taken from authentication)";
            log.warn(errorMessage);
        }
    }

    public void logout() {
        var authentication = getCurrentAuthentication();
        if (authentication != null) {
            authentication.setAuthenticated(false);
        }
        SecurityContextHolder.clearContext();
    }

    private Authentication getCurrentAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication;
    }

    private User getUserFromPrincipal(Object principal) {
        if (principal instanceof User user) {
            return user;
        }
        return null;
    }
}
