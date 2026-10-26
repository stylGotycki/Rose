package net.rose.rose_access_service.core.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public JwtAuthenticationEntryPoint(
            JsonMapper jsonMapper
    ) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );
        response.setContentType("application/json");

        jsonMapper.writeValue(
                response.getOutputStream(),
                Map.of(
                        "status", 401,
                        "code", "UNAUTHORIZED",
                        "message", "Authentication is required"
                )
        );
    }
}