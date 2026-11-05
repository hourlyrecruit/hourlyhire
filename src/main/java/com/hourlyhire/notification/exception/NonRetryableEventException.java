package com.hourlyhire.notification.exception;

/** Thrown for messages that can never succeed (bad JSON, missing fields), so retrying is pointless. */
public class NonRetryableEventException extends RuntimeException {

    public NonRetryableEventException(String message) {
        super(message);
    }

    public NonRetryableEventException(String message, Throwable cause) {
        super(message, cause);
    }
}