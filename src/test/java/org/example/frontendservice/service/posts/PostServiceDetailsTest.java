package org.example.frontendservice.service.posts;

import com.google.protobuf.Timestamp;
import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.utils.PostsUtils;
import org.example.postservice.grpc.PostServiceGrpc;
import org.example.postservice.grpc.PostServiceOuterClass;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@SpringBootTest
public class PostServiceDetailsTest {

    @InjectMocks
    private PostServiceDetails postServiceDetails;

    @Mock
    private PostServiceGrpc.PostServiceBlockingStub blockingStub;

    @Spy
    private PostsUtils postsUtils;

    private final ZoneId zoneId = ZoneId.systemDefault();

    private AutoCloseable openingOfMocks;

    @BeforeMethod
    public void setUp() {

        if (openingOfMocks == null) {
            openingOfMocks = MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(blockingStub, postsUtils);
        }
    }

    @Test
    public void testGetById() {

        var id = 1L;
        var content = "test_content";
        var createdAt = LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS);
        var userId = 11L;

        var postResponse = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(id)
                .setContent(content)
                .setCreatedAt(toTimestamp(createdAt))
                .setUserId(userId)
                .build();

        var postResponseDto = postsUtils.toPostResponseDto(postResponse);

        when(blockingStub.getById(any(PostServiceOuterClass.IdRequest.class))).thenReturn(postResponse);

        var result = postServiceDetails.getById(id);
        assertNotNull(result);
        assertEquals(result, postResponseDto);
        assertEquals(result.getId(), id);
        assertEquals(result.getContent(), content);

        verify(blockingStub, times(1)).getById(any());
        verify(postsUtils, times(2)).toPostResponseDto(any());
    }

    @Test
    public void testGetById_NotFound() {

        var id = -1L;

        var postResponse = PostServiceOuterClass.PostResponse.newBuilder().build();
        var postResponseDto = postsUtils.toPostResponseDto(postResponse);

        when(blockingStub.getById(any(PostServiceOuterClass.IdRequest.class))).thenReturn(postResponse);

        var result = postServiceDetails.getById(id);
        assertEquals(postResponseDto, result);
        assertEquals(result.getContent(), "");

        verify(blockingStub, times(1)).getById(any());
        verify(postsUtils, times(2)).toPostResponseDto(any());
    }

    @Test
    public void testGetAll() {

        var postResponseDto1 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(1L)
                .setContent("test_content_1")
                .setCreatedAt(toTimestamp(LocalDateTime.now().minusMinutes(111).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(11L)
                .build();

        var postResponseDto2 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(2L)
                .setContent("test_content_2")
                .setCreatedAt(toTimestamp(LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(22L)
                .build();

        var postsResponseList = List.of(postResponseDto1, postResponseDto2);
        var postsResponse = PostServiceOuterClass.PostsResponse.newBuilder()
                .addAllPosts(postsResponseList)
                .build();

        var postsResponseDtoList = postsUtils.toPostsResponseDto(postsResponseList);

        when(blockingStub.getAll(any(PostServiceOuterClass.EmptyBody.class))).thenReturn(postsResponse);

        var result = postServiceDetails.getAll();
        assertNotNull(result);
        assertEquals(result, postsResponseDtoList);

        verify(blockingStub, times(1)).getAll(any());
        verify(postsUtils, times(2)).toPostsResponseDto(any());
    }

    @Test
    public void testGetAllByUserId() {

        var userId = 333L;

        var postResponseDto1 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(1L)
                .setContent("test_content_1")
                .setCreatedAt(toTimestamp(LocalDateTime.now().minusMinutes(111).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(userId)
                .build();

        var postResponseDto2 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(2L)
                .setContent("test_content_2")
                .setCreatedAt(toTimestamp(LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(userId)
                .build();

        var postsResponseList = List.of(postResponseDto1, postResponseDto2);
        var postsResponse = PostServiceOuterClass.PostsResponse.newBuilder()
                .addAllPosts(postsResponseList)
                .build();

        var postsResponseDtoList = postsUtils.toPostsResponseDto(postsResponseList);

        when(blockingStub.getAllByUserId(any(PostServiceOuterClass.IdRequest.class))).thenReturn(postsResponse);

        var result = postServiceDetails.getAllByUserId(userId);
        assertNotNull(result);
        assertEquals(result, postsResponseDtoList);

        verify(blockingStub, times(1)).getAllByUserId(any());
        verify(postsUtils, times(2)).toPostsResponseDto(any());
    }

    @Test
    public void testCreate() {

        var content = "new_test_content";
        var userId = 11L;

        var postRequest = PostServiceOuterClass.PostRequest.newBuilder()
                .setContent(content)
                .setUserId(userId)
                .build();

        var postRequestDto = new PostRequestDto(
                postRequest.getContent(),
                new User(postRequest.getUserId())
        );

        var postResponse = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(999L)
                .setContent(content)
                .setUserId(userId)
                .setCreatedAt(toTimestamp(LocalDateTime.now()))
                .build();

        var postResponseDto = postsUtils.toPostResponseDto(postResponse);

        when(blockingStub.create(any(PostServiceOuterClass.PostRequest.class))).thenReturn(postResponse);

        var result = postServiceDetails.create(postRequestDto);
        assertNotNull(result);
        assertEquals(result, postResponseDto);
        assertEquals(result.getId(), postResponseDto.getId());
        assertEquals(result.getContent(), content);
        assertEquals(result.getUserId(), userId);
        assertNotNull(result.getCreatedAt());

        verify(blockingStub, times(1)).create(any());
        verify(postsUtils, times(2)).toPostResponseDto(any());
    }

    @Test
    public void testDeleteById() {

        var emptyBody = PostServiceOuterClass.EmptyBody.newBuilder().build();
        when(blockingStub.deleteById(any(PostServiceOuterClass.IdRequest.class))).thenReturn(emptyBody);

        var id = 1L;
        postServiceDetails.deleteById(id);
        verify(blockingStub, times(1)).deleteById(any());
    }

    private Timestamp toTimestamp(@Nonnull LocalDateTime dateTime) {
        dateTime = dateTime.truncatedTo(ChronoUnit.SECONDS);
        var instant = dateTime.atZone(zoneId).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .build();
    }
}