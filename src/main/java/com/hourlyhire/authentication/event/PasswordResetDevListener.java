package com.hourlyhire.authentication.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@Profile("dev")
public class PasswordResetDevListener {

    @Value("${app.password-reset.frontend-url:http://localhost:3000/reset-password}")
    private String frontendUrl;

    // Default phase is AFTER_COMMIT: nothing is "sent" if the transaction rolls back.
    @TransactionalEventListener
    public void onResetRequested(PasswordResetRequestedEvent event) {
        log.info("[DEV ONLY] Password reset for {} -> {}?token={} (expires {})",
                event.email(), frontendUrl, event.rawToken(), event.expiresAt());
    }
}