package org.example.frontendservice.controller.converter;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.example.frontendservice.model.User;
import org.example.frontendservice.service.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToUserConverter implements Converter<String, User> {

    private final UserService userService;

    @Override
    public User convert(@Nonnull String source) {
        if (!source.startsWith("User(id=")) {
            return null;
        }
        var startOfId = 8; // after "User(id="
        var endOfId = source.indexOf(",");
        String idAsString = source.substring(startOfId, endOfId);

        var id = Long.parseLong(idAsString);

        return userService.getById(id);
    }
}
