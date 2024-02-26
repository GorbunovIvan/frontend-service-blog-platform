package org.example.frontendservice.service.comments;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.example.frontendservice.utils.modelsbinders.comments.ModelsBinderForComments;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentServiceDetails commentServiceDetails;
    @Spy
    private CommentServiceDummy commentServiceDummy;
    @Mock
    private ModelsBinderForComments modelsBinder;

    private List<Comment> comments;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(commentServiceDetails, commentServiceDummy, modelsBinder);
        }

        comments = List.of(
                new Comment("1111", new Post(1L), new User(2L), "test_content_1", LocalDateTime.now().minusDays(15)),
                new Comment("2222", new Post(2L), new User(3L), "test_content_2", LocalDateTime.now().minusHours(15)),
                new Comment("3333", new Post(1L), new User(3L), "test_content_3", LocalDateTime.now().minusMinutes(15)),
                new Comment("4444", new Post(3L), new User(1L), "test_content_4", LocalDateTime.now().minusSeconds(15))
        );

        // Mocking commentServiceDetails
        when(commentServiceDetails.getById("")).thenReturn(null);
        when(commentServiceDetails.getAllByPostId(anyLong())).thenReturn(comments.stream().map(CommentResponseDto::new).toList());
        when(commentServiceDetails.getAllByUserId(anyLong())).thenReturn(comments.stream().map(CommentResponseDto::new).toList());

        when(commentServiceDetails.create(any(CommentRequestDto.class))).thenAnswer(ans -> {
            var commentRequestDto = ans.getArgument(0, CommentRequestDto.class);
            var comment = commentRequestDto.toComment();
            comment.setId("999");
            return new CommentResponseDto(comment);
        });

        for (var comment : comments) {
            when(commentServiceDetails.getById(comment.getId())).thenReturn(new CommentResponseDto(comment));
        }

        // Mocking modelsBinder
        for (var comment : comments) {
            when(modelsBinder.toComment(new CommentResponseDto(comment))).thenReturn(comment);
        }

        when(modelsBinder.toComments(anyList())).thenAnswer(ans -> {
            List<CommentResponseDto> comments = ans.getArgument(0, List.class);
            return comments.stream()
                    .map(modelsBinder::toComment)
                    .collect(Collectors.toList());
        });
    }

    @Test
    public void testGetById() {

        for (var comment : comments) {
            var result = commentService.getById(comment.getId());
            assertNotNull(result);
            assertEquals(result, comment);
            verify(commentServiceDetails, times(1)).getById(comment.getId());
        }

        verify(commentServiceDetails, times(comments.size())).getById(anyString());
        verify(commentServiceDummy, never()).getById(anyString());
    }

    @Test
    public void testGetById_NotFound() {

        var id = "";

        var result = commentService.getById(id);
        assertNull(result);

        verify(commentServiceDetails, times(1)).getById(id);
        verify(commentServiceDummy, never()).getById(anyString());
        verify(modelsBinder, never()).toComment(any());
    }

    @Test
    public void testGetAllByPostId() {

        var postId = 999L;

        var result = commentService.getAllByPostId(postId);
        assertNotNull(result);
        assertEquals(result, comments);

        verify(commentServiceDetails, times(1)).getAllByPostId(postId);
        verify(commentServiceDummy, never()).getAllByPostId(anyLong());
        verify(modelsBinder, times(1)).toComments(any());
    }

    @Test
    public void testGetAllByUserId() {

        var userId = 999L;

        var result = commentService.getAllByUserId(userId);
        assertNotNull(result);
        assertEquals(result, comments);

        verify(commentServiceDetails, times(1)).getAllByUserId(userId);
        verify(commentServiceDummy, never()).getAllByUserId(anyLong());
        verify(modelsBinder, times(1)).toComments(any());
    }

    @Test
    public void testCreate() {

        var newComment = new CommentRequestDto(new Post(2L), new User(1L), "new_test_content", LocalDateTime.now().minusSeconds(9));

        when(modelsBinder.toComment(any(CommentResponseDto.class))).thenAnswer(ans -> {
            var commentResponseDto = ans.getArgument(0, CommentResponseDto.class);
            return new Comment(
                    commentResponseDto.getId(),
                    new Post(commentResponseDto.getPostId()),
                    new User(commentResponseDto.getUserId()),
                    commentResponseDto.getContent(),
                    commentResponseDto.getCreatedAt()
            );
        });

        var result = commentService.create(newComment);
        assertNotNull(result);
        assertEquals(result, newComment.toComment());

        verify(commentServiceDetails, times(1)).create(any());
        verify(commentServiceDummy, never()).create(any());
        verify(modelsBinder, times(1)).toComment(any());
    }

    @Test
    public void testDeleteById() {

        for (var comment : comments) {
            commentService.deleteById(comment.getId());
            verify(commentServiceDetails, times(1)).deleteById(comment.getId());
        }

        verify(commentServiceDetails, times(comments.size())).deleteById(anyString());
        verify(commentServiceDummy, never()).deleteById(anyString());
    }
}