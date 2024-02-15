package org.example.frontendservice.service.users.feignclients;

import org.example.frontendservice.model.dto.users.UserDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service-auth",
        url = "${user-service.auth.url}",
        fallback = UserServiceAuthFeignClientFallback.class
)
@Primary
public interface UserServiceAuthFeignClient {
    @PostMapping("/check-token")
    ResponseEntity<Boolean> checkToken(@RequestBody String token);
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserDetailsDto userDetailsDto);
}
