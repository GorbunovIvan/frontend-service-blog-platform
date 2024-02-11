package org.example.frontendservice.service.security;

import lombok.RequiredArgsConstructor;
import org.example.frontendservice.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username: " + username);
        }
        return user;
    }
}
