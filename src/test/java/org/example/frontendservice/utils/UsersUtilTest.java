package org.example.frontendservice.utils;

import org.example.frontendservice.model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class UsersUtilTest {

    private final UsersUtil usersUtil = new UsersUtil();

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private AutoCloseable openingOfMocks;

    private User user;

    @BeforeMethod
    void setUp() {

        user = new User(1L);
        user.setUsername("test");

        openingOfMocks = MockitoAnnotations.openMocks(this);

        mockAuthentication(true);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterMethod
    void tearDown() throws Exception {
        openingOfMocks.close();
    }

    @Test
    void testGetCurrentUser_Authenticated() {

        mockAuthentication(true);

        var result = usersUtil.getCurrentUser();
        assertNotNull(result);
        assertEquals(result, user);

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, times(1)).getPrincipal();
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {

        mockAuthentication(false);

        var result = usersUtil.getCurrentUser();
        assertNull(result);

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, never()).getPrincipal();
    }

    @Test
    void testLogout() {
        mockAuthentication(true);
        usersUtil.logout();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authentication, times(1)).setAuthenticated(false);
    }

    @Test
    void testUpdateFieldsOfCurrentUser() {

        mockAuthentication(true);

        var newUser = new User("new_username", "new_password", LocalDate.now(), "+123456789");

        usersUtil.updateFieldsOfCurrentUser(newUser);

        verify(authentication, times(1)).getPrincipal();

        var userAfterUpdating = usersUtil.getCurrentUser();
        assertEquals(userAfterUpdating.getUsername(), newUser.getUsername());
        assertEquals(userAfterUpdating.getPassword(), newUser.getPassword());
        assertEquals(userAfterUpdating.getBirthDate(), newUser.getBirthDate());
        assertEquals(userAfterUpdating.getPhoneNumber(), newUser.getPhoneNumber());
    }

    @Test
    void testUpdateFieldsOfCurrentUser_NotAuthenticated() {
        mockAuthentication(false);
        assertThrows(IllegalStateException.class, () -> usersUtil.updateFieldsOfCurrentUser(new User(2L)));
        verify(securityContext, times(1)).getAuthentication();

    }

    private void mockAuthentication(boolean isAuthenticated) {

        when(authentication.isAuthenticated()).thenReturn(isAuthenticated);

        if (isAuthenticated) {
            when(authentication.getPrincipal()).thenReturn(user);
        } else {
            when(authentication.getPrincipal()).thenReturn(null);
        }
    }
}