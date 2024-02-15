package org.example.frontendservice.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.utils.UsersUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceShallow implements UserService {

    private final UserServiceDetails userServiceDetails;

    private final UsersUtil usersUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userServiceDetails.getAll();
    }

    @Override
    public User getById(Long id) {
        return userServiceDetails.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userServiceDetails.getByUsername(username);
    }

    @Override
    public User create(UserRequestDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userServiceDetails.create(user);
    }

    @Override
    public User update(Long id, UserRequestDto user) {

        if (!Optional.ofNullable(user.getPassword()).orElse("").isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        var userUpdated = userServiceDetails.update(id, user);

        usersUtil.updateFieldsOfCurrentUser(userUpdated);

        return userUpdated;
    }

    @Override
    public void deleteById(Long id) {
        userServiceDetails.deleteById(id);
    }
}
