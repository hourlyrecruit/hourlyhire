package com.hourlyhire.security.exceptionHandler;

import tools.jackson.databind.ObjectMapper;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException{
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(
                ErrorCode.ACCESS_DENIED,
                "You do not have permission to perform this action.",
                List.of(),
                request.getRequestURI(),
                MDC.get("correlationId")
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);
    }
}
