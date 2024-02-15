package org.example.frontendservice.config;

import feign.RequestInterceptor;
import lombok.extern.log4j.Log4j2;
import org.example.frontendservice.model.dto.users.UserDetailsDto;
import org.example.frontendservice.service.users.feignclients.UserServiceAuthFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

@Configuration
@EnableFeignClients(basePackages = "org.example.frontendservice.service.users.feignclients")
@Log4j2
public class FeignClientConfig {

    @Value("${user-service.auth.username}")
    private String userServiceAuthUsername;

    @Value("${user-service.auth.password}")
    private String userServiceAuthPassword;

    @Value("${user-service.auth.requestHeader}")
    private String userServiceAuthRequestHeader;

    private String jwToken;

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor(@Autowired UserServiceAuthFeignClient userServiceAuthFeignClient) {
        return requestTemplate -> {

            // Checking JWT
            var jwTokenIsCorrect = checkJwToken(userServiceAuthFeignClient);
            if (!jwTokenIsCorrect) {

                updateJwToken(userServiceAuthFeignClient);

                // Checking JWT again after authorization
                jwTokenIsCorrect = checkJwToken(userServiceAuthFeignClient);
                if (!jwTokenIsCorrect) {
                    return;
                }
            }

            requestTemplate.header(this.userServiceAuthRequestHeader, this.jwToken);
        };
    }

    private void updateJwToken(UserServiceAuthFeignClient userServiceAuthFeignClient) {

        var userDetails = new UserDetailsDto(userServiceAuthUsername, userServiceAuthPassword);
        var response = userServiceAuthFeignClient.login(userDetails);

        if (response.getStatusCode().isError()) {
            this.jwToken = null;
            log.error("Unable to authorize in the user-service. {}", response.getBody());
            return;
        }

        if (response.getBody() instanceof Map<?, ?> responseBody) {
            this.jwToken = (String) responseBody.get("token");
        }

        if (this.jwToken != null && !this.jwToken.isBlank()) {
            log.info("Token was received from user-service");
            return;
        }

        this.jwToken = null;
        log.error("Failed to get token during authorization in the user-service. {}", response.getBody());
    }

    private boolean checkJwToken(UserServiceAuthFeignClient userServiceAuthFeignClient) {

        if (Optional.ofNullable(this.jwToken).orElse("").isEmpty()) {
            log.info("JWT is empty");
            return false;
        }

        try {
            var response = userServiceAuthFeignClient.checkToken(this.jwToken);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                if (Boolean.TRUE.equals(response.getBody())) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error during checking JWT. {}", e.getMessage());
        }

        log.info("JWT is not correct");

        return false;
    }
}
