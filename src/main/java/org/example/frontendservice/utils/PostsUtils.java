package org.example.frontendservice.utils;

import com.google.protobuf.Timestamp;
import jakarta.annotation.Nonnull;
import org.example.frontendservice.model.dto.posts.PostRequestDto;
import org.example.frontendservice.model.dto.posts.PostResponseDto;
import org.example.postservice.grpc.PostServiceOuterClass;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostsUtils {

    private final ZoneId zoneId = ZoneId.systemDefault();

    public PostServiceOuterClass.PostRequest toPostRequest(@Nonnull PostRequestDto postRequestDto) {
        return PostServiceOuterClass.PostRequest.newBuilder()
                .setContent(postRequestDto.getContent())
                .setUserId(postRequestDto.getUserId())
                .build();
    }

    public PostResponseDto toPostResponseDto(@Nonnull PostServiceOuterClass.PostResponse postResponse) {
        return new PostResponseDto(
                postResponse.getId(),
                postResponse.getContent(),
                toLocalDateTime(postResponse.getCreatedAt()),
                postResponse.getUserId()
        );
    }

    public List<PostResponseDto> toPostsResponseDto(@Nonnull List<PostServiceOuterClass.PostResponse> postsResponse) {
        return postsResponse.stream()
                .map(this::toPostResponseDto)
                .collect(Collectors.toList());
    }

    protected Timestamp toTimestamp(@Nonnull LocalDateTime dateTime) {
        dateTime = dateTime.truncatedTo(ChronoUnit.SECONDS);
        var instant = dateTime.atZone(zoneId).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .build();
    }

    protected LocalDateTime toLocalDateTime(@Nonnull Timestamp timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), 0, zoneId.getRules().getStandardOffset(Instant.now()));
    }
}
