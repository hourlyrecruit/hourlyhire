package com.hourlyhire.authentication.dto;

import com.hourlyhire.authentication.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @Schema(example = "user@example.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 254, message = "Email must not exceed 254 characters.")
    private String email;

    @Schema(example = "Password@123")
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72,
            message = "Password must be between 8 and 72 characters.")
    private String password;

    @Schema(description = "Person's name. Duplicates are allowed.",
            example = "User")
    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    @Pattern(regexp = "^[\\p{L}][\\p{L} .'-]*$",
            message = "Name may contain letters, spaces, dots, hyphens and apostrophes.")
    private String username;

    @Schema(example = "WORKER")
    @NotNull(message = "Role is required (WORKER or EMPLOYER).")
    private Role role;
}