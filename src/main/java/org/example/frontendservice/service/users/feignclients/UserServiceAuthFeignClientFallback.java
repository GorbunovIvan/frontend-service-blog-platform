package org.example.frontendservice.service.users.feignclients;

import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.dto.users.UserDetailsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserServiceAuthFeignClientFallback implements UserServiceAuthFeignClient {

    private final String errorMessage = "Remote user-service-auth is not available.";

    @Override
    public ResponseEntity<Boolean> checkToken(String token) {
        log.error(errorMessage);
        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(UserDetailsDto userDetailsDto) {
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
