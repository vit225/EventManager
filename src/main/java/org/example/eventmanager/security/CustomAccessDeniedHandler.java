package org.example.eventmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eventmanager.exception.ServerMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        log.error("Handling access denied exception", accessDeniedException);
        var messageResponse = new ServerMessageDto(
                "Недостаточно прав для выполнения операции",
                accessDeniedException.getMessage(),
                LocalDateTime.now()
        );

        var stringResponse = objectMapper.writeValueAsString(messageResponse);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(stringResponse);
    }
}