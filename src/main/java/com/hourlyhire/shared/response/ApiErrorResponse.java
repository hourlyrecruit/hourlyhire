package com.hourlyhire.shared.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        boolean success,
        ErrorDetails error,
        Instant timestamp,
        String path,
        String correlationId
) {
    public record ErrorDetails(
            String code,
            String message,
            List<FieldErrorDetail> details
    ) {
    }

    public static ApiErrorResponse of(
            String code,
            String message,
            List<FieldErrorDetail> details,
            String path,
            String correlationId
    ) {
        return new ApiErrorResponse(
                false,
                new ErrorDetails(code, message, details),
                Instant.now(),
                path,
                correlationId
        );
    }
}
