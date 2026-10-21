
package com.hourlyhire.shared.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("Worker", "123");
        }

        @GetMapping("/test/business-error")
        public void businessError() {
            throw new BusinessException(
                    "BOOKING_CONFLICT",
                    "This booking conflicts with an existing booking."
            );
        }

        public record TestRequest(
                @NotBlank(message = "Name is required.")
                String name
        ) {
        }

        @PostMapping(
                value = "/test/validation",
                consumes = MediaType.APPLICATION_JSON_VALUE
        )
        public void validateRequest(@Valid @RequestBody TestRequest request) {
        }
    }

    @Test
    void shouldReturn404WhenResourceIsNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/test/not-found"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }

    @Test
    void shouldReturn409ForBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-error"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("BOOKING_CONFLICT"))
                .andExpect(jsonPath("$.path").value("/test/business-error"));
    }

    @Test
    void shouldReturn400WhenRequestValidationFails() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/test/validation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {"name": ""}
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.details[0].field").value("name"))
                .andExpect(jsonPath("$.path").value("/test/validation"));
    }
}
