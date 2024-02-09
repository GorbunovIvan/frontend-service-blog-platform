package org.example.frontendservice.service;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    List<User> getAll();
    User getById(Long id);
    User getByUsername(String username);
    User create(UserRequestDto user);
    User update(Long id, UserRequestDto user);
    void deleteById(Long id);
}
