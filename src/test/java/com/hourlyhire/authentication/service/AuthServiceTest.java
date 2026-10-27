package com.hourlyhire.authentication.service;

import com.hourlyhire.authentication.dto.RegisterRequest;
import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldNormalizeEmailAndSaveHashedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("  Worker@Example.com ");
        request.setPassword("a-strong-password");

        when(userAccountRepository.existsByEmail("worker@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("a-strong-password"))
                .thenReturn("hashed-password");

        when(userAccountRepository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> {
                    UserAccount account = invocation.getArgument(0);
                    return account;
                });

        // UserAccount ID database generate karti hai; is unit test mein
        // save hone aur hashed password use hone ko verify karenge.
        assertDoesNotThrow(() -> authService.register(request));

        verify(userAccountRepository)
                .existsByEmail("worker@example.com");
        verify(passwordEncoder).encode("a-strong-password");
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("worker@example.com");
        request.setPassword("a-strong-password");

        when(userAccountRepository.existsByEmail("worker@example.com"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        verify(userAccountRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
