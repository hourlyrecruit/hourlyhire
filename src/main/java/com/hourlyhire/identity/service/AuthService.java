
package com.hourlyhire.identity.service;

import com.hourlyhire.identity.dto.RegisterRequest;
import com.hourlyhire.identity.entity.UserAccount;
import com.hourlyhire.identity.repository.UserAccountRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long register(RegisterRequest request) {

        String normalizedEmail =
                request.getEmail().trim().toLowerCase();

        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        UserAccount account = new UserAccount();

        account.setEmail(normalizedEmail);

        // Use the email prefix as the initial username.
        account.setUsername(
                normalizedEmail.substring(0, normalizedEmail.indexOf("@")));

        account.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        account.setAccountStatus("ACTIVE");
        account.setRole("WORKER");

        UserAccount savedAccount =
                userAccountRepository.save(account);

        return savedAccount.getId();
    }
}
