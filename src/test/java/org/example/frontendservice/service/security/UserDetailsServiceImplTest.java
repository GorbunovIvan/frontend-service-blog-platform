package org.example.frontendservice.service.security;

import org.example.frontendservice.model.User;
import org.example.frontendservice.service.users.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserService userService;

    private User user;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userService);
        }

        user = new User("new_username", "new_password", LocalDate.now(), "+123456789");

        when(userService.getByUsername(user.getUsername())).thenReturn(user);
    }

    @Test
    void testLoadUserByUsername() {

        var username = user.getUsername();

        var result = userDetailsService.loadUserByUsername(username);
        assertNotNull(result);
        assertEquals(result, user);
        verify(userService, times(1)).getByUsername(username);
    }

    @Test
    void testLoadUserByUsername_NotFound() {

        var username = "";

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
        verify(userService, times(1)).getByUsername(username);
    }
}