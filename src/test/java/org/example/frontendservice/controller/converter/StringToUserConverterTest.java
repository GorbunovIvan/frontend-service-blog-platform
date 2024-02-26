package org.example.frontendservice.controller.converter;

import org.example.frontendservice.model.User;
import org.example.frontendservice.service.users.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@SpringBootTest
public class StringToUserConverterTest {

    @InjectMocks
    private StringToUserConverter converter;

    @Mock
    private UserService userService;

    private List<User> users;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userService);
        }

        users = List.of(
                new User(1L, "username_1", "password_1", LocalDate.now().minusYears(25), "+1111199999"),
                new User(2L, "username_2", "password_2", LocalDate.now().minusMonths(25), "+222228888"),
                new User(3L, "username_3", "password_3", LocalDate.now().minusWeeks(25), "+3333377777"),
                new User(4L, "username_4", "password_4", LocalDate.now().minusDays(25), "+44444666666")
        );

        when(userService.getById(-1L)).thenReturn(null);

        for (var user : users) {
            when(userService.getById(user.getId())).thenReturn(user);
        }
    }

    @Test
    public void testConvert() {

        for (var user : users) {
            var source = user.toString();
            var result = converter.convert(source);
            assertEquals(result, user);
            verify(userService, times(1)).getById(user.getId());
        }

        verify(userService, times(users.size())).getById(anyLong());
    }

    @Test
    public void testConvert_NotFound() {
        var source = "some stuff";
        var result = converter.convert(source);
        assertNull(result);
        verify(userService, never()).getById(anyLong());
    }
}