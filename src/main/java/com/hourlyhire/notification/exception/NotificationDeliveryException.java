package com.hourlyhire.notification.exception;

/** At least one channel failed to deliver. Thrown so Kafka retries (and eventually dead-letters) the event. */
public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(String message) {
        super(message);
    }
}