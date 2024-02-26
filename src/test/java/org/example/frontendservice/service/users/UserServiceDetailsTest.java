package org.example.frontendservice.service.users;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.service.users.feignclients.UserServiceFeignClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class UserServiceDetailsTest {

    @InjectMocks
    private UserServiceDetails userServiceDetails;

    @Mock
    private UserServiceFeignClient userServiceFeignClient;

    private List<User> users;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userServiceFeignClient);
        }

        users = List.of(
                new User(1L, "username_1", "password_1", LocalDate.now().minusYears(25), "+1111199999"),
                new User(2L, "username_2", "password_2", LocalDate.now().minusMonths(25), "+222228888"),
                new User(3L, "username_3", "password_3", LocalDate.now().minusWeeks(25), "+3333377777"),
                new User(4L, "username_4", "password_4", LocalDate.now().minusDays(25), "+44444666666")
        );

        when(userServiceFeignClient.getAll()).thenReturn(ResponseEntity.ok(users));
        when(userServiceFeignClient.getById(-1L)).thenReturn(ResponseEntity.notFound().build());
        when(userServiceFeignClient.getByUsername("")).thenReturn(ResponseEntity.notFound().build());

        when(userServiceFeignClient.create(any(UserRequestDto.class))).thenAnswer(ans -> {
            var userRequest = ans.getArgument(0, UserRequestDto.class);
            var newUser = userRequest.toUser();
            newUser.setId(999L);
            return ResponseEntity.ok(newUser);
        });

        when(userServiceFeignClient.update(anyLong(), any(UserRequestDto.class))).thenAnswer(ans -> {
            var id = ans.getArgument(0, Long.class);
            var userRequest = ans.getArgument(1, UserRequestDto.class);
            var newUser = userRequest.toUser();
            newUser.setId(id);
            return ResponseEntity.ok(newUser);
        });

        for (var user : users) {
            when(userServiceFeignClient.getById(user.getId())).thenReturn(ResponseEntity.ok(user));
            when(userServiceFeignClient.getByUsername(user.getUsername())).thenReturn(ResponseEntity.ok(user));
        }
    }

    @Test
    public void testGetAll() {
        var result = userServiceDetails.getAll();
        assertNotNull(result);
        assertEquals(result, users);
        verify(userServiceFeignClient, times(1)).getAll();
    }

    @Test
    public void testGetById() {

        for (var user : users) {
            var result = userServiceDetails.getById(user.getId());
            assertNotNull(result);
            assertEquals(result, user);
            verify(userServiceFeignClient, times(1)).getById(user.getId());
        }

        verify(userServiceFeignClient, times(users.size())).getById(anyLong());
    }

    @Test
    public void testGetById_NotFound() {

        var id = -1L;

        var result = userServiceDetails.getById(id);
        assertNull(result);
        verify(userServiceFeignClient, times(1)).getById(id);
    }

    @Test
    public void testGetByUsername() {

        for (var user : users) {
            var result = userServiceDetails.getByUsername(user.getUsername());
            assertNotNull(result);
            assertEquals(result, user);
            verify(userServiceFeignClient, times(1)).getByUsername(user.getUsername());
        }

        verify(userServiceFeignClient, times(users.size())).getByUsername(anyString());
    }

    @Test
    public void testGetByUsername_NotFound() {

        var username = "";

        var result = userServiceDetails.getByUsername(username);
        assertNull(result);
        verify(userServiceFeignClient, times(1)).getByUsername(username);
    }

    @Test
    public void testCreate() {

        var userRequestDto = new UserRequestDto("new_username", "new_password", LocalDate.now().minusYears(14), "+987654321");

        var result = userServiceDetails.create(userRequestDto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result, userRequestDto.toUser());

        verify(userServiceFeignClient, times(1)).create(any());
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

            var result = userServiceDetails.update(user.getId(), userRequestDto);
            assertNotNull(result);
            assertEquals(result.getId(), user.getId());
            assertEquals(result, userRequestDto.toUser());

            verify(userServiceFeignClient, times(1)).update(user.getId(), userRequestDto);
        }

        verify(userServiceFeignClient, times(users.size())).update(anyLong(), any());
    }

    @Test
    public void testUpdate_NotFound() {
        when(userServiceFeignClient.update(anyLong(), any(UserRequestDto.class))).thenThrow(ResponseStatusException.class);
        assertThrows(ResponseStatusException.class, () -> userServiceDetails.update(-1L, new UserRequestDto()));
        verify(userServiceFeignClient, times(1)).update(anyLong(), any());
    }

    @Test
    public void testDeleteById() {
        for (var user : users) {
            userServiceDetails.deleteById(user.getId());
            verify(userServiceFeignClient, times(1)).deleteById(user.getId());
        }
        verify(userServiceFeignClient, atLeastOnce()).deleteById(anyLong());
        verify(userServiceFeignClient, times(users.size())).deleteById(anyLong());
    }
}