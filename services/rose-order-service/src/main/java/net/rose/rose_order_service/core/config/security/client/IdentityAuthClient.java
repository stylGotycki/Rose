package net.rose.rose_order_service.core.config.security.client;

import net.rose.rose_order_service.core.config.restclient.IdentityAuthProperties;
import net.rose.rose_order_service.core.exception.InvalidTokenException;
import net.rose.rose_order_service.core.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class IdentityAuthClient {

    private final RestClient restClient;

    public IdentityAuthClient(
            IdentityAuthProperties properties
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.authUrl())
                .build();
    }

    public IdentityCurrentUserResponse getCurrentUser(
            String token
    ) {
        try {
            IdentityCurrentUserResponse response =
                    restClient.get()
                            .uri("/api/auth/me")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + token
                            )
                            .retrieve()
                            .body(IdentityCurrentUserResponse.class);

            if (response == null
                    || response.id() == null
                    || response.email() == null) {
                throw InvalidTokenException.invalid();
            }

            if (!"ACTIVE".equals(response.status())) {
                throw new UnauthorizedException(
                        "User account is not active"
                );
            }

            return response;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw InvalidTokenException.invalid();
            }

            throw new UnauthorizedException(
                    "Could not verify token in identity service"
            );
        }
    }
}