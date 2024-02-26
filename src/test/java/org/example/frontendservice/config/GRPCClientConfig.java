package org.example.frontendservice.config;

import io.grpc.ManagedChannelBuilder;
import org.example.postservice.grpc.PostServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GRPCClientConfig {

    @Value("${post-service.grpc.host}")
    private String grpcHost;

    @Value("${post-service.grpc.port}")
    private int port;

    @Bean
    public PostServiceGrpc.PostServiceBlockingStub blockingStub() {

        var channel = ManagedChannelBuilder
                .forAddress(grpcHost, port)
                .usePlaintext()
                .build();

        return PostServiceGrpc.newBlockingStub(channel);
    }
}
