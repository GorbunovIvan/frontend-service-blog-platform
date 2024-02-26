package org.example.frontendservice.utils.modelsbinders.users;

import org.example.frontendservice.model.Comment;
import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.service.comments.CommentServiceShallow;
import org.example.frontendservice.service.posts.PostServiceShallow;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@SpringBootTest
public class ModelsBinderForUsersImplTest {

    @InjectMocks
    private ModelsBinderForUsersImpl modelsBinder;

    @Mock
    private PostServiceShallow postService;
    @Mock
    private CommentServiceShallow commentService;

    private List<User> users;
    private List<Post> posts;
    private List<Comment> comments;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(postService, commentService);
        }

        users = List.of(
                new User(1L, "username_1", "password_1", LocalDate.now().minusYears(25), "+1111199999"),
                new User(2L, "username_2", "password_2", LocalDate.now().minusMonths(25), "+222228888"),
                new User(3L, "username_3", "password_3", LocalDate.now().minusWeeks(25), "+3333377777"),
                new User(4L, "username_4", "password_4", LocalDate.now().minusDays(25), "+44444666666")
        );

        posts = List.of(
                new Post(1L, "post_content_1", LocalDateTime.now().minusMonths(25), users.get(3)),
                new Post(2L, "post_content_2", LocalDateTime.now().minusWeeks(25), users.get(1)),
                new Post(3L, "post_content_3", LocalDateTime.now().minusDays(25), users.get(3)),
                new Post(4L, "post_content_4", LocalDateTime.now().minusHours(25), users.get(1)),
                new Post(5L, "post_content_5", LocalDateTime.now().minusMinutes(25), users.get(2)),
                new Post(6L, "post_content_6", LocalDateTime.now().minusSeconds(25), users.get(3))
        );

        comments = List.of(
                new Comment("111", posts.get(1), users.get(2), "comment_content_1", LocalDateTime.now().minusDays(25)),
                new Comment("222", posts.get(1), users.get(1), "comment_content_2", LocalDateTime.now().minusHours(25)),
                new Comment("333", posts.get(0), users.get(0), "comment_content_3", LocalDateTime.now().minusMinutes(25)),
                new Comment("444", posts.get(3), users.get(0), "comment_content_4", LocalDateTime.now().minusSeconds(25))
        );

        when(postService.getAllByUser(new User())).thenReturn(Collections.emptyList());
        when(commentService.getAllByUser(new User())).thenReturn(Collections.emptyList());

        for (var user : users) {

            var postsByUser = posts.stream().filter(p -> p.getUser().equals(user)).toList();
            var commentsByUser = comments.stream().filter(c -> c.getUser().equals(user)).toList();

            when(postService.getAllByUser(user)).thenReturn(postsByUser);
            when(commentService.getAllByUser(user)).thenReturn(commentsByUser);
        }
    }

    @Test
    void testAddDependencyFieldsToUser() {

        for (var user : users) {

            var postsByUserExpected = posts.stream().filter(p -> p.getUser().equals(user)).toList();
            var commentsByUserExpected = comments.stream().filter(c -> c.getUser().equals(user)).toList();

            var result = modelsBinder.addDependencyFieldsToUser(user);
            assertEquals(result, user);

            assertEquals(user.getPosts(), postsByUserExpected);
            assertEquals(user.getComments(), commentsByUserExpected);

            for (var post : user.getPosts()) {
                assertEquals(post.getUser(), user);
            }

            for (var comment : user.getComments()) {
                assertEquals(comment.getUser(), user);
            }

            verify(postService, times(1)).getAllByUser(user);
            verify(commentService, times(1)).getAllByUser(user);
        }

        verify(postService, times(users.size())).getAllByUser(any());
        verify(commentService, times(users.size())).getAllByUser(any());
    }

    @Test
    void testAddDependencyFieldsToUsers() {

        var result = modelsBinder.addDependencyFieldsToUsers(users);
        assertEquals(result, users);

        for (var user : users) {

            var postsByUserExpected = posts.stream().filter(p -> p.getUser().equals(user)).toList();
            var commentsByUserExpected = comments.stream().filter(c -> c.getUser().equals(user)).toList();

            assertEquals(user.getPosts(), postsByUserExpected);
            assertEquals(user.getComments(), commentsByUserExpected);

            for (var post : user.getPosts()) {
                assertEquals(post.getUser(), user);
            }

            for (var comment : user.getComments()) {
                assertEquals(comment.getUser(), user);
            }

            verify(postService, times(1)).getAllByUser(user);
            verify(commentService, times(1)).getAllByUser(user);
        }

        verify(postService, times(users.size())).getAllByUser(any());
        verify(commentService, times(users.size())).getAllByUser(any());
    }
}