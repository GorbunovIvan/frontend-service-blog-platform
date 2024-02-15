package org.example.frontendservice.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.service.users.feignclients.UserServiceFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceDetails {

    private final UserServiceFeignClient userServiceFeignClient;

    public List<User> getAll() {

        var response = userServiceFeignClient.getAll();
        checkResponseEntity(response, "Unable to get all users from the user-service");

        var users = response.getBody();
        if (users == null) {
            return Collections.emptyList();
        }

        return users;
    }
    
    public User getById(Long id) {
        var response = userServiceFeignClient.getById(id);
        checkResponseEntity(response, "Unable to get user by id '" + id + "' from the user-service", false);
        return response.getBody();
    }

    public User getByUsername(String username) {
        var response = userServiceFeignClient.getByUsername(username);
        checkResponseEntity(response, "Unable to get user by username '" + username + "' from the user-service", false);
        return response.getBody();
    }
    
    public User create(UserRequestDto user) {

        var response = userServiceFeignClient.create(user);
        checkResponseEntity(response, "Unable to create a user with the user-service");
        var userPersisted = response.getBody();

        log.info("New user was registered - {}", userPersisted);

        return userPersisted;
    }
    
    public User update(Long id, UserRequestDto user) {

        var response = userServiceFeignClient.update(id, user);
        checkResponseEntity(response, "Unable to update a user (id=" + id + ") with the user-service");
        var userUpdated = response.getBody();

        log.info("User (id={}) was updated - {}", id, userUpdated);

        return userUpdated;
    }

    public void deleteById(Long id) {
        userServiceFeignClient.deleteById(id);
        log.info("User (id={}) was deleted", id);
    }

    private void checkResponseEntity(ResponseEntity<?> responseEntity, String errorMessage) {
        checkResponseEntity(responseEntity, errorMessage, true);
    }

    private void checkResponseEntity(ResponseEntity<?> responseEntity, String errorMessage, boolean throwMinorErrors) {

        var statusCode = responseEntity.getStatusCode();
        if (!statusCode.isError()) {
            return;
        }

        errorMessage = String.format("%s. %s", errorMessage, responseEntity.getBody());
        log.error(errorMessage);

        boolean isErrorMinor = statusCode.equals(HttpStatus.NOT_FOUND);

        if (throwMinorErrors || !isErrorMinor) {
            throw new ResponseStatusException(statusCode, errorMessage);
        }
    }
}
