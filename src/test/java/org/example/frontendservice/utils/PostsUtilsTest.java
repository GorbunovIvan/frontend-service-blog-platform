package org.example.frontendservice.utils;

import org.example.frontendservice.model.User;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.postservice.grpc.PostServiceOuterClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PostsUtilsTest {

    private final PostsUtils postsUtils = new PostsUtils();

    @Test
    void testToPostRequest() {

        var content = "test_content";
        var createdAt = LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS);
        var user = new User(11L);

        var postRequestDto = new PostRequestDto(content, createdAt, user);

        var result = postsUtils.toPostRequest(postRequestDto);

        assertNotNull(result);
        assertEquals(result.getContent(), content);
        assertEquals(result.getUserId(), user.getId());
    }

    @Test
    void testToPostResponseDto() {

        var id = 1L;
        var content = "test_content";
        var createdAt = LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS);
        var userId = 11L;

        var postResponseDto = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(id)
                .setContent(content)
                .setCreatedAt(postsUtils.toTimestamp(createdAt))
                .setUserId(userId)
                .build();

        var result = postsUtils.toPostResponseDto(postResponseDto);

        assertNotNull(result);
        assertEquals(result.getId(), id);
        assertEquals(result.getContent(), content);
        assertEquals(result.getCreatedAt(), createdAt);
        assertEquals(result.getUserId(), userId);
    }

    @Test
    void testToPostsResponseDto() {

        var postResponseDto1 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(1L)
                .setContent("test_content_1")
                .setCreatedAt(postsUtils.toTimestamp(LocalDateTime.now().minusMinutes(111).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(11L)
                .build();

        var postResponseDto2 = PostServiceOuterClass.PostResponse.newBuilder()
                .setId(2L)
                .setContent("test_content_2")
                .setCreatedAt(postsUtils.toTimestamp(LocalDateTime.now().minusMinutes(222).truncatedTo(ChronoUnit.SECONDS)))
                .setUserId(22L)
                .build();

        var postsResponseDtoList = List.of(postResponseDto1, postResponseDto2);

        var result = postsUtils.toPostsResponseDto(postsResponseDtoList);

        assertNotNull(result);
        assertEquals(result.size(), postsResponseDtoList.size());

        for (int i = 0; i < result.size(); i++) {

            var postDto = postsResponseDtoList.get(i);
            var postResult = result.get(i);

            assertEquals(postResult.getId(), postDto.getId());
            assertEquals(postResult.getContent(), postDto.getContent());
            assertEquals(postResult.getCreatedAt(), postsUtils.toLocalDateTime(postDto.getCreatedAt()));
            assertEquals(postResult.getUserId(), postDto.getUserId());
        }
    }
}