package com.hourlyhire.authentication.service;

import com.hourlyhire.authentication.dto.RegisterRequest;
import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hourlyhire.shared.exception.BusinessException;

import java.util.Locale;

import static com.hourlyhire.shared.enums.ErrorCode.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long register(RegisterRequest request) {
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userAccountRepository.existsByEmail(email)) {
            throw new BusinessException(
                    EMAIL_ALREADY_EXISTS,
                    "An account with this email already exists."
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        UserAccount userAccount =
                new UserAccount(email, passwordHash);

        UserAccount savedAccount =
                userAccountRepository.save(userAccount);

        return savedAccount.getId();
    }
}
