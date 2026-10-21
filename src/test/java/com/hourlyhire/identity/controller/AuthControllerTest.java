package com.hourlyhire.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hourlyhire.identity.security.SecurityConfig;
import org.springframework.context.annotation.Import;
import com.hourlyhire.identity.dto.RegisterRequest;
import com.hourlyhire.identity.service.AuthService;
import com.hourlyhire.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;



    @Test
    void registerShouldReturn201ForValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("worker@example.com");
        request.setPassword("a-strong-password");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(42L);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(42));
    }

    @Test
    void registerShouldReturn400ForInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("a-strong-password");

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        org.mockito.Mockito.verifyNoInteractions(authService);
    }
}

