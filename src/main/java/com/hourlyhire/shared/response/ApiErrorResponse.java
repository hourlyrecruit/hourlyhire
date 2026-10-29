package com.hourlyhire.shared.response;

import com.hourlyhire.shared.enums.ErrorCode;

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
            ErrorCode code,
            String message,
            List<FieldErrorDetail> details
    ) {
    }

    public static ApiErrorResponse of(
            ErrorCode code,
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