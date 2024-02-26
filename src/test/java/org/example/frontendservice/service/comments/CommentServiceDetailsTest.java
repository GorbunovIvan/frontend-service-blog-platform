package org.example.frontendservice.service.comments;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class CommentServiceDetailsTest {

    @InjectMocks
    private CommentServiceDetails commentServiceDetails;

    @Mock
    private HttpGraphQlClient httpGraphQlClient;
    @Mock
    private GraphQlClient.RequestSpec requestSpec;
    @Mock
    private GraphQlClient.RetrieveSpec retrieveSpec;
    @Mock
    private ClientGraphQlResponse graphQlResponse;
    @Mock
    private ClientResponseField clientResponseField;

    private Set<Comment> comments;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(httpGraphQlClient, requestSpec, retrieveSpec, graphQlResponse, clientResponseField);
        }

        comments = Set.of(
                new Comment("1111", new Post(1L), new User(2L), "test_content_1", LocalDateTime.now().minusDays(15)),
                new Comment("2222", new Post(2L), new User(3L), "test_content_2", LocalDateTime.now().minusHours(15)),
                new Comment("3333", new Post(1L), new User(3L), "test_content_3", LocalDateTime.now().minusMinutes(15)),
                new Comment("4444", new Post(3L), new User(1L), "test_content_4", LocalDateTime.now().minusSeconds(15))
        );

        when(httpGraphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(requestSpec.execute()).thenReturn(Mono.just(graphQlResponse));
        when(graphQlResponse.field(anyString())).thenReturn(clientResponseField);
    }

    @Test
    void testGetById() {

        for (var comment : comments) {

            var commentResponseDto = new CommentResponseDto(comment);
            when(retrieveSpec.toEntity(any(Class.class))).thenReturn(Mono.just(commentResponseDto));

            var result = commentServiceDetails.getById(comment.getId());
            assertNotNull(result);
            assertEquals(result, commentResponseDto);
        }

        verify(httpGraphQlClient, times(comments.size())).document(anyString());
        verify(requestSpec, times(comments.size())).retrieve("getById");
        verify(retrieveSpec, times(comments.size())).toEntity(CommentResponseDto.class);
    }

    @Test
    void testGetById_NotFound() {

        var id = "";

        when(retrieveSpec.toEntity(any(Class.class))).thenReturn(Mono.empty());

        var result = commentServiceDetails.getById(id);
        assertNull(result);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getById");
        verify(retrieveSpec, times(1)).toEntity(CommentResponseDto.class);
    }

    @Test
    void testGetAllByPostId() {

        var posts = comments.stream()
                .map(Comment::getPost)
                .distinct()
                .toList();

        assertFalse(posts.isEmpty());

        for (var post : posts) {

            var postId = post.getId();

            var commentsExpected = comments.stream()
                    .filter(comment -> comment.getPostId().equals(postId))
                    .toList();

            var commentsResponseDto = commentsExpected.stream()
                    .map(CommentResponseDto::new)
                    .toList();

            when(retrieveSpec.toEntityList(any(Class.class))).thenReturn(Mono.just(commentsResponseDto));

            var result = commentServiceDetails.getAllByPostId(postId);
            assertNotNull(result);
            assertEquals(new HashSet<>(result), new HashSet<>(commentsResponseDto));
        }

        verify(httpGraphQlClient, times(posts.size())).document(anyString());
        verify(requestSpec, times(posts.size())).retrieve("getAllByPostId");
        verify(retrieveSpec, times(posts.size())).toEntityList(CommentResponseDto.class);
    }

    @Test
    void testGetAllByUserId() {

        var users = comments.stream()
                .map(Comment::getUser)
                .distinct()
                .toList();

        assertFalse(users.isEmpty());

        for (var user : users) {

            var userId = user.getId();

            var commentsExpected = comments.stream()
                    .filter(comment -> comment.getUserId().equals(userId))
                    .toList();

            var commentsResponseDto = commentsExpected.stream()
                    .map(CommentResponseDto::new)
                    .toList();

            when(retrieveSpec.toEntityList(any(Class.class))).thenReturn(Mono.just(commentsResponseDto));

            var result = commentServiceDetails.getAllByUserId(userId);
            assertNotNull(result);
            assertEquals(new HashSet<>(result), new HashSet<>(commentsResponseDto));
        }

        verify(httpGraphQlClient, times(users.size())).document(anyString());
        verify(requestSpec, times(users.size())).retrieve("getAllByUserId");
        verify(retrieveSpec, times(users.size())).toEntityList(CommentResponseDto.class);
    }

    @Test
    void testCreate() {

        var newComment = new Comment("9999", new Post(2L), new User(1L), "new_test_content", LocalDateTime.now().minusSeconds(9));
        var newCommentRequestDto = new CommentRequestDto(newComment);
        var newCommentResponseDto = new CommentResponseDto(newComment);

        when(clientResponseField.toEntity(any(Class.class))).thenReturn(newCommentResponseDto);

        var result = commentServiceDetails.create(newCommentRequestDto);
        assertNotNull(result);
        assertEquals(result, newCommentResponseDto);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).execute();
        verify(graphQlResponse, times(1)).field("create");
        verify(clientResponseField, times(1)).toEntity(CommentResponseDto.class);
    }

    @Test
    void testDeleteById() {

        var id = "999";

        commentServiceDetails.deleteById(id);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).execute();
    }
}