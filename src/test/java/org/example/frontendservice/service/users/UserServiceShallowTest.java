package org.example.frontendservice.service.users;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.utils.UsersUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class UserServiceShallowTest {

    @InjectMocks
    private UserServiceShallow userService;

    @Mock
    private UserServiceDetails userServiceDetails;
    @Mock
    private UsersUtil usersUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    private List<User> users;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userServiceDetails, usersUtil, passwordEncoder);
        }

        users = List.of(
                new User(1L, "username_1", "password_1", LocalDate.now().minusYears(25), "+1111199999"),
                new User(2L, "username_2", "password_2", LocalDate.now().minusMonths(25), "+222228888"),
                new User(3L, "username_3", "password_3", LocalDate.now().minusWeeks(25), "+3333377777"),
                new User(4L, "username_4", "password_4", LocalDate.now().minusDays(25), "+44444666666")
        );

        when(userServiceDetails.getAll()).thenReturn(users);
        when(userServiceDetails.getById(-1L)).thenReturn(null);
        when(userServiceDetails.getByUsername("")).thenReturn(null);

        when(userServiceDetails.create(any(UserRequestDto.class))).thenAnswer(ans -> {
            var userRequest = ans.getArgument(0, UserRequestDto.class);
            var newUser = userRequest.toUser();
            newUser.setId(999L);
            return newUser;
        });

        when(userServiceDetails.update(anyLong(), any(UserRequestDto.class))).thenAnswer(ans -> {
            var id = ans.getArgument(0, Long.class);
            var userRequest = ans.getArgument(1, UserRequestDto.class);
            var newUser = userRequest.toUser();
            newUser.setId(id);
            return newUser;
        });

        for (var user : users) {
            when(userServiceDetails.getById(user.getId())).thenReturn(user);
            when(userServiceDetails.getByUsername(user.getUsername())).thenReturn(user);
        }

        when(passwordEncoder.encode(anyString())).thenAnswer(ans -> {
            var arg = ans.getArgument(0, String.class);
            return String.format("$encoded$_%s_$encoded$", arg);
        });
    }

    @Test
    public void testGetAll() {
        var result = userService.getAll();
        assertNotNull(result);
        assertEquals(users, result);
        verify(userServiceDetails, times(1)).getAll();
    }

    @Test
    public void testGetById() {

        for (var user : users) {
            var result = userService.getById(user.getId());
            assertNotNull(result);
            assertEquals(user, result);
            verify(userServiceDetails, times(1)).getById(user.getId());
        }

        verify(userServiceDetails, times(users.size())).getById(anyLong());
    }

    @Test
    public void testGetById_NotFound() {

        var id = -1L;

        var result = userService.getById(id);
        assertNull(result);
        verify(userServiceDetails, times(1)).getById(id);
    }

    @Test
    public void testGetByUsername() {

        for (var user : users) {
            var result = userService.getByUsername(user.getUsername());
            assertNotNull(result);
            assertEquals(user, result);
            verify(userServiceDetails, times(1)).getByUsername(user.getUsername());
        }

        verify(userServiceDetails, times(users.size())).getByUsername(anyString());
    }

    @Test
    public void testGetByUsername_NotFound() {

        var username = "";

        var result = userService.getByUsername(username);
        assertNull(result);
        verify(userServiceDetails, times(1)).getByUsername(username);
    }

    @Test
    public void testCreate() {

        var userRequestDto = new UserRequestDto("new_username", "new_password", LocalDate.now().minusYears(14), "+987654321");

        var password = userRequestDto.getPassword();

        var result = userService.create(userRequestDto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userRequestDto.toUser(), result);
        assertEquals(userRequestDto.getBirthDate(), result.getBirthDate());
        assertEquals(userRequestDto.toUser().getPhoneNumber(), result.getPhoneNumber());

        verify(userServiceDetails, times(1)).create(any());
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    public void testUpdate() {

        for (var user : users) {

            var userRequestDto = new UserRequestDto(
                    user.getUsername() + "_updated",
                    user.getPassword() + "_updated",
                    user.getBirthDate().minusYears(1),
                    user.getPhoneNumber() + "0"
            );

            var newPassword = userRequestDto.getPassword();

            var result = userService.update(user.getId(), userRequestDto);
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(userRequestDto.toUser(), result);
            assertEquals(userRequestDto.getBirthDate(), result.getBirthDate());
            assertEquals(userRequestDto.toUser().getPhoneNumber(), result.getPhoneNumber());

            assertNotNull(result.getPassword());
            assertFalse(result.getPassword().isBlank());
            assertNotEquals(newPassword, result.getPassword()); // it should be encoded already

            verify(userServiceDetails, times(1)).update(user.getId(), userRequestDto);
            verify(passwordEncoder, times(1)).encode(newPassword);
        }

        verify(userServiceDetails, times(users.size())).update(anyLong(), any());
        verify(usersUtil, times(users.size())).updateFieldsOfCurrentUser(any(User.class));
        verify(passwordEncoder, times(users.size())).encode(anyString());
    }

    @Test
    public void testUpdate_NotFound() {
        when(userServiceDetails.update(anyLong(), any(UserRequestDto.class))).thenThrow(ResponseStatusException.class);
        assertThrows(ResponseStatusException.class, () -> userService.update(-1L, new UserRequestDto()));
        verify(userServiceDetails, times(1)).update(anyLong(), any());
        verify(usersUtil, never()).updateFieldsOfCurrentUser(any(User.class));
    }

    @Test
    public void testUpdate_PasswordWasNotChanged() {

        var user = users.get(0);

        var userRequestDto = new UserRequestDto(
                user.getUsername() + "_updated",
                null,
                user.getBirthDate().minusYears(1),
                user.getPhoneNumber() + "0"
        );

        var result = userService.update(user.getId(), userRequestDto);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(userRequestDto.toUser(), result);
        assertEquals(userRequestDto.getBirthDate(), result.getBirthDate());
        assertEquals(userRequestDto.toUser().getPhoneNumber(), result.getPhoneNumber());

        verify(userServiceDetails, times(1)).update(user.getId(), userRequestDto);
        verify(usersUtil, times(1)).updateFieldsOfCurrentUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testDeleteById() {
        for (var user : users) {
            userService.deleteById(user.getId());
            verify(userServiceDetails, times(1)).deleteById(user.getId());
        }
        verify(userServiceDetails, atLeastOnce()).deleteById(anyLong());
        verify(userServiceDetails, times(users.size())).deleteById(anyLong());
    }
}