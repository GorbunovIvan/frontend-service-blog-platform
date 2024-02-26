package org.example.frontendservice.controller.converter;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.service.posts.PostService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@SpringBootTest
public class StringToPostConverterTest {

    @InjectMocks
    private StringToPostConverter converter;

    @Mock
    private PostService postService;

    private List<Post> posts;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(postService);
        }

        posts = List.of(
                new Post(1L, "test_content_1", LocalDateTime.now().minusDays(71L), new User(2L)),
                new Post(2L, "test_content_2", LocalDateTime.now().minusHours(71L), new User(1L)),
                new Post(3L, "test_content_3", LocalDateTime.now().minusMinutes(71L), new User(2L)),
                new Post(4L, "test_content_4", LocalDateTime.now().minusSeconds(71L), new User(3L))
        );

        when(postService.getById(-1L)).thenReturn(null);

        for (var post : posts) {
            when(postService.getById(post.getId())).thenReturn(post);
        }
    }

    @Test
    public void testConvert() {

        for (var post : posts) {
            var source = post.toString();
            var result = converter.convert(source);
            assertEquals(result, post);
            verify(postService, times(1)).getById(post.getId());
        }

        verify(postService, times(posts.size())).getById(anyLong());
    }

    @Test
    public void testConvert_NotFound() {
        var source = "some stuff";
        var result = converter.convert(source);
        assertNull(result);
        verify(postService, never()).getById(anyLong());
    }
}