package org.example.frontendservice.service.users.feignclients;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class UserServiceFeignClientFallback implements UserServiceFeignClient {

    private final String errorMessage = "Remote user-service is not available.";

    @Override
    public ResponseEntity<List<User>> getAll() {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<User> getById(Long id) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<User> getByUsername(String username) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<User> create(UserRequestDto user) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<User> update(Long id, UserRequestDto user) {
        log.error(errorMessage);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public void deleteById(Long id) {
        log.error(errorMessage);
    }
}
