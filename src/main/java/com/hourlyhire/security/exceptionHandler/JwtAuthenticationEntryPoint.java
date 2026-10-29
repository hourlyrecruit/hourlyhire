package com.hourlyhire.security.exceptionHandler;

import tools.jackson.databind.ObjectMapper;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.response.ApiErrorResponse;
import com.hourlyhire.shared.response.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ApiErrorResponse apiError = ApiErrorResponse.of(
                ErrorCode.UNAUTHORIZED,
                "Authentication is required to access this resource.",
                List.of(),
                request.getRequestURI(),
                MDC.get("correlationId")
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiError);
    }
}
