package net.rose.rose_access_service.core.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

@Component
public class RestAccessDeniedHandler
        implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public RestAccessDeniedHandler(
            JsonMapper jsonMapper
    ) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException {
        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );
        response.setContentType("application/json");

        jsonMapper.writeValue(
                response.getOutputStream(),
                Map.of(
                        "status", 403,
                        "code", "FORBIDDEN",
                        "message", "Access denied"
                )
        );
    }
}