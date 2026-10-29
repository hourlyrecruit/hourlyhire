package com.hourlyhire.shared.response;

public record FieldErrorDetail(
        String field,
        String code,
        String message
) {
}
