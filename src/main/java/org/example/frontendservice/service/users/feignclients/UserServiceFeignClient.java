package org.example.frontendservice.service.users.feignclients;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        fallback = UserServiceFeignClientFallback.class
)
@Primary
public interface UserServiceFeignClient {

    @GetMapping
    ResponseEntity<List<User>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<User> getById(@PathVariable Long id);

    @GetMapping("/username/{username}")
    ResponseEntity<User> getByUsername(@PathVariable String username);

    @PostMapping
    ResponseEntity<User> create(@RequestBody UserRequestDto user);

    @PutMapping("/{id}")
    ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserRequestDto user);

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id);
}
