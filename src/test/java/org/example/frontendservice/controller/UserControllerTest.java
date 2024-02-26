package org.example.frontendservice.controller;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.users.UserRequestDto;
import org.example.frontendservice.service.users.UserService;
import org.example.frontendservice.utils.UsersUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertNotEquals;

@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @Mock
    private UsersUtil usersUtil;

    private final String baseURL = "/users";

    private List<User> users;

    private User userToUseAsCurrent;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userService, usersUtil);
        }

        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(new UserController(userService, usersUtil))
                    .build();
        }

        users = List.of(
                new User(1L, "username_1", "password_1", LocalDate.now().minusYears(15), "+111999"),
                new User(2L, "username_2", "password_2", LocalDate.now().minusMonths(15), "+222666"),
                new User(3L, "username_3", "password_3", LocalDate.now().minusWeeks(15), "+333777"),
                new User(4L, "username_4", "password_4", LocalDate.now().minusDays(15), "+444888")
        );

        userToUseAsCurrent = users.get(users.size()-1);

        // Mocking UserService
        when(userService.getAll()).thenReturn(users);

        for (var user : users) {
            when(userService.getById(user.getId())).thenReturn(user);
            when(userService.getByUsername(user.getUsername())).thenReturn(user);
        }

        // Authorizing current user
        when(usersUtil.getCurrentUser()).thenReturn(userToUseAsCurrent);
    }

    @Test
    public void testGetAll() throws Exception {

        mockMvc.perform(get(baseURL))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(model().attribute("users", users));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void testGetSelfPage() throws Exception {

        mockMvc.perform(get(baseURL + "/self"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + userToUseAsCurrent.getId()));

        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testGetSelfPage_Unauthorized() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get(baseURL + "/self"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testGetById() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        for (var user : users) {

            mockMvc.perform(get(baseURL + "/{id}", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("users/user"))
                    .andExpect(model().attribute("user", user))
                    .andExpect(model().attribute("isOwnPage", false));

            verify(userService, times(1)).getById(user.getId());
        }

        verify(userService, times(users.size())).getById(anyLong());
        verify(usersUtil, atLeast(users.size())).getCurrentUser();
    }

    @Test
    public void testGetById_NotFound() throws Exception {

        var id = -1L;

        mockMvc.perform(get(baseURL + "/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(id);
    }

    @Test
    public void testGetById_CurrentUser() throws Exception {

        mockMvc.perform(get(baseURL + "/{id}", userToUseAsCurrent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user"))
                .andExpect(model().attribute("user", userToUseAsCurrent))
                .andExpect(model().attribute("isOwnPage", true));

        verify(userService, times(1)).getById(userToUseAsCurrent.getId());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testEdit() throws Exception {

        mockMvc.perform(get(baseURL + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + userToUseAsCurrent.getId() + "/edit"));

        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testEdit_Unauthorized() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get(baseURL + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testEditById() throws Exception {

        mockMvc.perform(get(baseURL + "/{id}/edit", userToUseAsCurrent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit"))
                .andExpect(model().attribute("id", userToUseAsCurrent.getId()));

        verify(userService, times(1)).getById(userToUseAsCurrent.getId());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testEditById_NotFound() throws Exception {

        var id = -1L;

        mockMvc.perform(get(baseURL + "/{id}/edit", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(id);
    }

    @Test
    public void testEditById_Unauthorized() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get(baseURL + "/{id}/edit", userToUseAsCurrent.getId()))
                .andExpect(status().isForbidden());

        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testEditById_ButNotCurrentUser() throws Exception {

        var id = users.get(0).getId();
        assertNotEquals(userToUseAsCurrent.getId(), id);

        mockMvc.perform(get(baseURL + "/{id}/edit", id))
                .andExpect(status().isForbidden());

        verify(userService, times(1)).getById(id);
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testUpdate() throws Exception {

        var userRequestDto = new UserRequestDto(userToUseAsCurrent);

        mockMvc.perform(patch(baseURL + "/{id}", userToUseAsCurrent.getId())
                        .param("username", userRequestDto.getUsername())
                        .param("password", userRequestDto.getPassword())
                        .param("birthDate", userRequestDto.getBirthDate().toString())
                        .param("phoneNumber", userRequestDto.getPhoneNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/" + userToUseAsCurrent.getId()));

        verify(userService, times(1)).getById(userToUseAsCurrent.getId());
        verify(userService, times(1)).update(anyLong(), any(UserRequestDto.class));
        verify(userService, times(1)).update(userToUseAsCurrent.getId(), userRequestDto);
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testUpdate_NotFound() throws Exception {

        var id = -1L;

        mockMvc.perform(patch(baseURL + "/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(id);
        verify(userService, never()).update(anyLong(), any(UserRequestDto.class));
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testUpdate_Unauthorized() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        for (var user : users) {

            mockMvc.perform(patch(baseURL + "/{id}", user.getId()))
                    .andExpect(status().isForbidden());

            verify(userService, times(1)).getById(user.getId());
        }

        verify(userService, times(users.size())).getById(anyLong());
        verify(userService, never()).update(anyLong(), any(UserRequestDto.class));
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testUpdate_ButNotCurrentUser() throws Exception {

        var id = users.get(0).getId();
        assertNotEquals(id, userToUseAsCurrent.getId());

        mockMvc.perform(patch(baseURL + "/{id}", id))
                .andExpect(status().isForbidden());

        verify(userService, never()).update(anyLong(), any(UserRequestDto.class));
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(delete(baseURL + "/{id}", userToUseAsCurrent.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"));

        verify(userService, times(1)).deleteById(userToUseAsCurrent.getId());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
        verify(usersUtil, times(1)).logout();
    }

    @Test
    public void testDelete_NotCurrentUser() throws Exception {

        var id = users.get(0).getId();
        assertNotEquals(userToUseAsCurrent.getId(), id);

        mockMvc.perform(delete(baseURL + "/{id}", id))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
        verify(usersUtil, never()).logout();
    }

    @Test
    public void testDelete_Unauthorized() throws Exception {

        when(usersUtil.getCurrentUser()).thenReturn(null);

        for (var user : users) {
            mockMvc.perform(delete(baseURL + "/{id}", user.getId()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/users"));
        }

        verify(userService, never()).deleteById(anyLong());
        verify(usersUtil, atLeast(users.size())).getCurrentUser();
        verify(usersUtil, never()).logout();
    }
}