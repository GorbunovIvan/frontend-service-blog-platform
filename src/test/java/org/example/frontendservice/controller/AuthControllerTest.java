package org.example.frontendservice.controller;

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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @Mock
    private UsersUtil usersUtil;

    private final String baseURL = "/auth";

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
                    .standaloneSetup(new AuthController(userService, usersUtil))
                    .build();
        }
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get(baseURL + "/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    public void testRegister_Page() throws Exception {
        mockMvc.perform(get(baseURL + "/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute("user", new UserRequestDto()));
    }

    @Test
    public void testRegister() throws Exception {

        var userRequestDto = new UserRequestDto("new_username", "new_password", LocalDate.now(), "+000");

        mockMvc.perform(post(baseURL + "/register")
                    .param("username", userRequestDto.getUsername())
                    .param("password", userRequestDto.getPassword())
                    .param("birthDate", userRequestDto.getBirthDate().toString())
                    .param("phoneNumber", userRequestDto.getPhoneNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        verify(userService, times(1)).create(userRequestDto);
        verify(usersUtil, times(1)).logout();
    }
}