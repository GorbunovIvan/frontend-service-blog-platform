package org.example.frontendservice.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.utils.UsersUtil;
import org.example.frontendservice.utils.modelsbinders.users.ModelsBinderForUsersImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserServiceDetails userServiceDetails;

    private final ModelsBinderForUsersImpl modelsBinder;

    private final UsersUtil usersUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        var users = userServiceDetails.getAll();
        modelsBinder.addDependencyFieldsToUsers(users);
        return users;
    }

    @Override
    public User getById(Long id) {
        var user = userServiceDetails.getById(id);
        modelsBinder.addDependencyFieldsToUser(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        var user = userServiceDetails.getByUsername(username);
        modelsBinder.addDependencyFieldsToUser(user);
        return user;
    }

    @Override
    public User create(UserRequestDto user) {

        if (Optional.ofNullable(user.getUsername()).orElse("").isBlank()) {
            throw new IllegalArgumentException("Username is empty");
        }
        if (Optional.ofNullable(user.getPassword()).orElse("").isBlank()) {
            throw new IllegalArgumentException("Password is empty");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var userPersisted = userServiceDetails.create(user);
        modelsBinder.addDependencyFieldsToUser(userPersisted);

        return userPersisted;
    }

    @Override
    public User update(Long id, UserRequestDto user) {

        if (!Optional.ofNullable(user.getPassword()).orElse("").isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        var userUpdated = userServiceDetails.update(id, user);
        modelsBinder.addDependencyFieldsToUser(userUpdated);

        usersUtil.updateFieldsOfCurrentUser(userUpdated);

        return userUpdated;
    }

    @Override
    public void deleteById(Long id) {
        userServiceDetails.deleteById(id);
    }
}
