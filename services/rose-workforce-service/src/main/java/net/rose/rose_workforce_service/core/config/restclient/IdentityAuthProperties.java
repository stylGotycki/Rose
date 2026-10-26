package net.rose.rose_workforce_service.core.config.restclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.identity")
public record IdentityAuthProperties(
        String authUrl
) {
}