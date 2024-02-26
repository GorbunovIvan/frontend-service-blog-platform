package org.example.frontendservice.service.posts;

import org.example.frontendservice.model.Post;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.frontendservice.utils.modelsbinders.posts.ModelsBinderForPostsImpl;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostServiceDetails postServiceDetails;
    @Spy
    private PostServiceDummy postServiceDummy;
    @Mock
    private ModelsBinderForPostsImpl modelsBinder;

    private List<Post> posts;

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(postServiceDetails, postServiceDummy, modelsBinder);
        }

        posts = List.of(
                new Post(1L, "test_content_1", LocalDateTime.now().minusDays(71L), new User(2L)),
                new Post(2L, "test_content_2", LocalDateTime.now().minusHours(71L), new User(1L)),
                new Post(3L, "test_content_3", LocalDateTime.now().minusMinutes(71L), new User(2L)),
                new Post(4L, "test_content_4", LocalDateTime.now().minusSeconds(71L), new User(3L))
        );

        List<PostResponseDto> postsResponsesDto = posts.stream()
                .map(PostResponseDto::new)
                .toList();

        when(postServiceDetails.getAll()).thenReturn(postsResponsesDto);
        when(postServiceDetails.getAllByUserId(anyLong())).thenReturn(postsResponsesDto);
        when(postServiceDetails.getById(-1L)).thenReturn(null);
        when(postServiceDetails.create(any(PostRequestDto.class))).thenAnswer(ans -> {
            var postDto = ans.getArgument(0, PostRequestDto.class);
            var postCreated = postDto.toPost();
            postCreated.setId(999L);
            return new PostResponseDto(postCreated);
        });

        for (var postResponseDto : postsResponsesDto) {
            when(postServiceDetails.getById(postResponseDto.getId())).thenReturn(postResponseDto);
        }

        when(modelsBinder.toPost(any(PostResponseDto.class))).thenAnswer(ans -> {
            var postDto = ans.getArgument(0, PostResponseDto.class);
            if (postDto == null) {
                return null;
            }
            return new Post(
                    postDto.getId(),
                    postDto.getContent(),
                    postDto.getCreatedAt(),
                    new User(postDto.getUserId())
            );
        });

        when(modelsBinder.toPosts(any())).thenAnswer(ans -> {
            List<PostResponseDto> postsDto = ans.getArgument(0, List.class);
            return postsDto.stream()
                    .map(modelsBinder::toPost)
                    .collect(Collectors.toList());
        });
    }

    @Test
    public void testGetById() {

        for (var post : posts) {
            var result = postService.getById(post.getId());
            assertEquals(result, post);
            verify(postServiceDetails, times(1)).getById(post.getId());
        }

        verify(postServiceDetails, times(posts.size())).getById(anyLong());
        verify(postServiceDummy, never()).getById(anyLong());
        verify(modelsBinder, times(posts.size())).toPost(any());
        verify(modelsBinder, times(posts.size())).addDependencyFieldsToPost(any());
    }

    @Test
    public void testGetById_NotFound() {

        var id = -1L;

        var result = postService.getById(id);
        assertNull(result);

        verify(postServiceDetails, times(1)).getById(id);
        verify(postServiceDetails, times(1)).getById(anyLong());
        verify(postServiceDummy, never()).getById(anyLong());
        verify(modelsBinder, never()).toPost(any());
        verify(modelsBinder, never()).addDependencyFieldsToPost(any());
    }

    @Test
    public void testGetAll() {

        var result = postService.getAll();
        assertEquals(result, posts);

        verify(postServiceDetails, times(1)).getAll();
        verify(postServiceDummy, never()).getAll();
        verify(modelsBinder, times(1)).toPosts(any());
        verify(modelsBinder, times(result.size())).addDependencyFieldsToPost(any());
    }

    @Test
    public void testGetAllByUserId() {

        var id = 1L;

        var result = postService.getAllByUserId(id);
        assertEquals(result, posts);

        verify(postServiceDetails, times(1)).getAllByUserId(anyLong());
        verify(postServiceDummy, never()).getAllByUserId(anyLong());
        verify(modelsBinder, times(1)).toPosts(any());
        verify(modelsBinder, times(result.size())).addDependencyFieldsToPost(any());
    }

    @Test
    public void testCreate() {

        var content = "new_test_content";
        var userId = 11L;

        var postRequest = new PostRequestDto(content, new User(userId));

        var result = postService.create(postRequest);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result.getContent(), content);

        assertNotNull(result.getUser());
        assertEquals(result.getUserId(), userId);

        verify(postServiceDetails, times(1)).create(any());
        verify(postServiceDummy, never()).create(any());
        verify(modelsBinder, times(1)).toPost(any());
        verify(modelsBinder, times(1)).addDependencyFieldsToPost(any());
    }

    @Test
    public void testDeleteById() {

        for (var post : posts) {
            postService.deleteById(post.getId());
            verify(postServiceDetails, times(1)).deleteById(post.getId());
            verify(postServiceDummy, never()).deleteById(post.getId());
        }

        verify(postServiceDetails, times(posts.size())).deleteById(anyLong());
        verify(postServiceDummy, never()).deleteById(anyLong());
    }
}