package org.example.frontendservice.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.dto.comments.CommentRequestDto;
import org.example.frontendservice.model.dto.comments.CommentResponseDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@ConditionalOnBean(name = "httpGraphQlClient")
@RequiredArgsConstructor
@Log4j2
public class CommentServiceDetails {

    private final HttpGraphQlClient httpGraphQlClient;

    public CommentResponseDto getById(String id) {

        String query = """
                {
                  getById(id: "%s") {
                    id
                    postId
                    userId
                    content
                    createdAt
                  }
                }
                """;

        query = String.format(query, id);

        var commentFuture = httpGraphQlClient.document(query)
                .retrieve("getById")
                .toEntity(CommentResponseDto.class)
                .toFuture();

        try {
            return commentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to get a comment - {}", e.getMessage());
            return null;
        }
    }

    public List<CommentResponseDto> getAllByPostId(Long postId) {

        String query = """
            {
              getAllByPostId(postId: %d) {
                id
                postId
                userId
                content
                createdAt
              }
            }
            """;

        query = String.format(query, postId);

        var commentFuture = httpGraphQlClient.document(query)
                .retrieve("getAllByPostId")
                .toEntityList(CommentResponseDto.class)
                .toFuture();

        try {
            return commentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to get comments - {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<CommentResponseDto> getAllByUserId(Long userId) {

            String query = """
            {
              getAllByUserId(userId: %d) {
                id
                postId
                userId
                content
                createdAt
              }
            }
            """;

        query = String.format(query, userId);

        var commentFuture = httpGraphQlClient.document(query)
                .retrieve("getAllByUserId")
                .toEntityList(CommentResponseDto.class)
                .toFuture();

        try {
            return commentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to get comments - {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public CommentResponseDto create(CommentRequestDto comment) {

        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        String query = """
            mutation {
              create(postId: %d, userId: %d, content: "%s", createdAt: "%s") {
                id
                postId
                userId
                content
                createdAt
              }
            }
            """;

        query = String.format(query,
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                DateTimeFormatter.ISO_DATE_TIME.format(comment.getCreatedAt()));

        var commentFuture = httpGraphQlClient.document(query)
                .execute()
                .toFuture();

        try {

            var commentEntity = commentFuture.get()
                    .field("create")
                    .toEntity(CommentResponseDto.class);

            log.info("New comment was added - {}", commentEntity);
            return commentEntity;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to create a comment - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String id) {

        String query = """
            mutation {
              deleteById(id: "%s")
            }
            """;

        query = String.format(query, id);

        httpGraphQlClient.document(query)
                .execute()
                .toFuture();
    }
}
