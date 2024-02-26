package org.example.frontendservice.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class RootControllerTest {

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(new RootController())
                    .build();
        }
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"));
    }
}