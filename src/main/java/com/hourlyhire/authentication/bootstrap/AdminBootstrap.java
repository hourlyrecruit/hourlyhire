package com.hourlyhire.authentication.bootstrap;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UserAccountRepository repo;
    private final PasswordEncoder encoder;

    @Value("${app.bootstrap-admin.email:}")
    private String email;
    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) return;

        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (repo.existsByEmail(normalized)) return;

        repo.save(new UserAccount(normalized, "Admin", encoder.encode(password), Role.ADMIN));
    }
}