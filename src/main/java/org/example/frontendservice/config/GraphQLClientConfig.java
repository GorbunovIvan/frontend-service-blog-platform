package org.example.frontendservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;

@Configuration
public class GraphQLClientConfig {

    @Value("${comment-service.graphql-url}")
    private String graphqlURL;

    @Bean
    public HttpGraphQlClient httpGraphQlClient() {
        return HttpGraphQlClient.builder()
                .url(graphqlURL)
                .build();
    }
}
