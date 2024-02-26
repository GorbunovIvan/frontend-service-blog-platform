package org.example.frontendservice.utils.modelsbinders.posts;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.frontendservice.service.comments.CommentServiceShallow;
import org.example.frontendservice.service.users.UserServiceShallow;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class ModelsBinderForPostsImplTest {

    @InjectMocks
    private ModelsBinderForPostsImpl modelsBinder;

    @Mock
    private UserServiceShallow userService;

    @Mock
    private CommentServiceShallow commentService;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userService, commentService);
        }

        when(userService.getById(anyLong())).thenAnswer(answer -> {
            long id = answer.getArgument(0);
            return new User(id);
        });
    }

    @Test
    void testToPost() {

        var id = 1L;
        var content = "test_content";
        var createdAt = LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS);
        var userId = 11L;

        var postResponseDto = new PostResponseDto(id, content, createdAt, userId);

        var result = modelsBinder.toPost(postResponseDto);

        assertNotNull(result);
        assertEquals(result.getId(), id);
        assertEquals(result.getContent(), content);
        assertEquals(result.getCreatedAt(), createdAt);

        assertNotNull(result.getUser());
        assertEquals(result.getUserId(), userId);
    }

    @Test
    void testToPost_Null() {
        var result = modelsBinder.toPost(null);
        assertNull(result);
    }

    @Test
    void testToPosts() {

        var postResponseDto1 = new PostResponseDto(
                1L,
                "test_content_1",
                LocalDateTime.now().minusMinutes(111).truncatedTo(ChronoUnit.SECONDS),
                11L);

        var postResponseDto2 = new PostResponseDto(
                2L,
                "test_content_2",
                LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS),
                22L);

        var postsResponseDtoList = List.of(postResponseDto1, postResponseDto2);

        var result = modelsBinder.toPosts(postsResponseDtoList);

        assertNotNull(result);
        assertEquals(result.size(), postsResponseDtoList.size());

        for (int i = 0; i < result.size(); i++) {

            var postDto = postsResponseDtoList.get(i);
            var postResult = result.get(i);

            assertEquals(postResult.getId(), postDto.getId());
            assertEquals(postResult.getContent(), postDto.getContent());
            assertEquals(postResult.getCreatedAt(), postDto.getCreatedAt());

            assertNotNull(postResult.getUser());
            assertEquals(postResult.getUserId(), postDto.getUserId());
        }
    }

    @Test
    void testAddDependencyFieldsToPost() {

        var comments = List.of(
                new Comment("111", new Post(11L), new User(22L), "comment_content_1", LocalDateTime.now().minusHours(3)),
                new Comment("222", new Post(33L), new User(33L), "comment_content_2", LocalDateTime.now().minusMinutes(3)),
                new Comment("333", new Post(22L), new User(11L), "comment_content_3", LocalDateTime.now().minusSeconds(3))
        );

        when(commentService.getAllByPostId(anyLong())).thenReturn(comments);

        var post = new Post(
                1L,
                "test_content",
                LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS),
                new User(11L));

        var result = modelsBinder.addDependencyFieldsToPost(post);
        assertEquals(result, post);

        assertEquals(post.getComments(), comments);

        for (var comment : post.getComments()) {
            assertEquals(comment.getPost(), post);
        }

        verify(commentService, times(1)).getAllByPostId(post.getId());
    }
}