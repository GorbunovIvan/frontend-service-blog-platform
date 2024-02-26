package org.example.frontendservice.controller;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.service.posts.PostService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;
    @Mock
    private UsersUtil usersUtil;

    private final String baseURL = "/posts";

    private final User userToUseAsCurrent = new User(99L, "test_username", "test_password", null, "+123");

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(postService, usersUtil);
        }

        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(new PostController(postService, usersUtil))
                    .build();
        }

        when(usersUtil.getCurrentUser()).thenReturn(userToUseAsCurrent);
    }

    @Test
    public void testGetById() throws Exception {

        var id = 1L;

        var post = new Post(id, "post content", LocalDateTime.now(), new User(2L, "unknown_user", "", null, ""));

        when(postService.getById(id)).thenReturn(post);

        mockMvc.perform(get(baseURL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/post"))
                .andExpect(model().attribute("post", post))
                .andExpect(model().attribute("isOwnPost", false));

        verify(postService, times(1)).getById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testGetById_PostOfCurrentUser() throws Exception {

        var id = 1L;

        var post = new Post(id, "post content", LocalDateTime.now(), userToUseAsCurrent);

        when(postService.getById(id)).thenReturn(post);

        mockMvc.perform(get(baseURL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/post"))
                .andExpect(model().attribute("post", post))
                .andExpect(model().attribute("isOwnPost", true));

        verify(postService, times(1)).getById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testGetById_NotFound() throws Exception {

        var id = 1L;

        mockMvc.perform(get(baseURL + "/{id}", id))
                .andExpect(status().isNotFound());

        verify(postService, times(1)).getById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testGetAll() throws Exception {

        var posts = List.of(
            new Post(1L, "post content 1", LocalDateTime.now(), new User(22L)),
            new Post(2L, "post content 2", LocalDateTime.now(), new User(33L)),
            new Post(3L, "post content 3", LocalDateTime.now(), new User(44L))
        );

        when(postService.getAll()).thenReturn(posts);

        mockMvc.perform(get(baseURL))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/posts"))
                .andExpect(model().attribute("posts", posts));

        verify(postService, times(1)).getAll();
    }

    @Test
    public void testCreate() throws Exception {

        var postDto = new PostRequestDto("new post", userToUseAsCurrent);

        mockMvc.perform(post(baseURL)
                        .param("content", postDto.getContent())
                        .param("createdAt", postDto.getCreatedAt().toString()))
//                        .param("user", postDto.getUser().toString())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/self"));

        verify(postService, times(1)).create(any(PostRequestDto.class));
    }

    @Test
    public void testDelete() throws Exception {

        var id = 999L;

        var post = new Post(id, "post content", LocalDateTime.now(), new User(2L, "unknown_user", "", null, ""));

        when(postService.getById(id)).thenReturn(post);

        mockMvc.perform(delete(baseURL + "/{id}", id))
                .andExpect(status().isForbidden());

        verify(postService, never()).deleteById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }

    @Test
    public void testDelete_byCurrentUser() throws Exception {

        var id = 999L;

        var post = new Post(id, "post content", LocalDateTime.now(), userToUseAsCurrent);

        when(postService.getById(id)).thenReturn(post);

        mockMvc.perform(delete(baseURL + "/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/self"));

        verify(postService, times(1)).deleteById(anyLong());
        verify(usersUtil, atLeastOnce()).getCurrentUser();
    }
}