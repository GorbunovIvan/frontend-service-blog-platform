package org.example.frontendservice.utils.modelsbinders.comments;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.example.frontendservice.service.posts.PostServiceShallow;
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

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class ModelsBinderForCommentsImplTest {

    @InjectMocks
    private ModelsBinderForCommentsImpl modelsBinder;

    @Mock
    private UserServiceShallow userService;
    @Mock
    private PostServiceShallow postService;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userService, postService);
        }

        when(userService.getById(anyLong())).thenAnswer(answer -> {
            long id = answer.getArgument(0);
            return new User(id);
        });

        when(postService.getById(anyLong())).thenAnswer(answer -> {
            long id = answer.getArgument(0);
            return new Post(id);
        });
    }

    @Test
    void testToComment() {

        var id = "111111";
        var postId = 11L;
        var userId = 111L;
        var content = "test_content";
        var createdAt = LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS);

        var commentResponseDto = new CommentResponseDto(id, postId, userId, content, createdAt);

        var result = modelsBinder.toComment(commentResponseDto);

        assertNotNull(result);
        assertEquals(result.getId(), id);
        assertEquals(result.getContent(), content);
        assertEquals(result.getCreatedAt(), createdAt);

        assertNotNull(result.getPost());
        assertEquals(result.getPostId(), postId);

        assertNotNull(result.getUser());
        assertEquals(result.getUserId(), userId);

        verify(userService, times(1)).getById(userId);
        verify(postService, times(1)).getById(postId);
    }

    @Test
    void testToComment_Null() {
        var result = modelsBinder.toComment(null);
        assertNull(result);
    }

    @Test
    void testToComments() {

        var commentResponseDto1 = new CommentResponseDto(
                "111999",
                11L,
                111L,
                "test_content_1",
                LocalDateTime.now().minusMinutes(111).truncatedTo(ChronoUnit.SECONDS));

        var commentResponseDto2 = new CommentResponseDto(
                "222888",
                22L,
                222L,
                "test_content_2",
                LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS));

        var commentsResponseDtoList = List.of(commentResponseDto1, commentResponseDto2);

        var result = modelsBinder.toComments(commentsResponseDtoList);

        assertNotNull(result);
        assertEquals(result.size(), commentsResponseDtoList.size());

        for (int i = 0; i < result.size(); i++) {

            var commentDto = commentsResponseDtoList.get(i);
            var commentResult = result.get(i);

            assertEquals(commentResult.getId(), commentDto.getId());
            assertEquals(commentResult.getContent(), commentDto.getContent());
            assertEquals(commentResult.getCreatedAt(), commentDto.getCreatedAt());

            assertNotNull(commentResult.getPost());
            assertEquals(commentResult.getPostId(), commentDto.getPostId());

            assertNotNull(commentResult.getUser());
            assertEquals(commentResult.getUserId(), commentDto.getUserId());
        }
    }
}