package org.example.frontendservice.controller;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.service.comments.CommentService;
import org.example.frontendservice.utils.UsersUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;
    @Mock
    private UsersUtil usersUtil;

    private final String baseURL = "/comments";

    private final User userToUseAsCurrent = new User(99L, "test_username", "test_password", null, "+123");

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(commentService, usersUtil);
        }

        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(new CommentController(commentService, usersUtil))
                    .build();
        }

        when(usersUtil.getCurrentUser()).thenReturn(userToUseAsCurrent);
    }

    @Test
    public void testCreate() throws Exception {

        var newComment = new CommentRequestDto(new Post(2L), userToUseAsCurrent, "new_test_content");

        mockMvc.perform(post(baseURL)
//                        .param("post", newComment.getPost().toString())
//                        .param("user", newComment.getUser().toString())
                        .param("content", newComment.getContent())
                        .param("createdAt", newComment.getCreatedAt().toString()))
                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/posts/" + newComment.getPostId()));
                .andExpect(view().name("redirect:/posts/null"));

        verify(commentService, times(1)).create(any(CommentRequestDto.class));
    }

    @Test
    public void testDelete() throws Exception {

        var id = "999";

        var commentFound = new Comment(
                id,
                new Post(2L),
                new User(2L, "unknown_user", "", null, ""),
                "new_test_content",
                LocalDateTime.now().minusSeconds(9));

        when(commentService.getById(id)).thenReturn(commentFound);

        mockMvc.perform(delete(baseURL + "/{id}", id))
                .andExpect(status().isForbidden());

        verify(commentService, never()).deleteById(anyString());
        verify(usersUtil, times(1)).getCurrentUser();
    }

    @Test
    public void testDelete_byCurrentUser() throws Exception {

        var id = "999";

        var commentFound = new Comment(
                id,
                new Post(2L),
                userToUseAsCurrent,
                "new_test_content",
                LocalDateTime.now().minusSeconds(9));

        when(commentService.getById(id)).thenReturn(commentFound);

        mockMvc.perform(delete(baseURL + "/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/self"));

        verify(commentService, times(1)).deleteById(anyString());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }
}