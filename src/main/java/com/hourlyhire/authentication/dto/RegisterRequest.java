package com.hourlyhire.authentication.dto;

import com.hourlyhire.authentication.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 254, message = "Email must not exceed 254 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72,
            message = "Password must be between 12 and 72 characters.")
    private String password;

    @NotNull(message = "Role is required (WORKER or EMPLOYER).")
    private Role role;
}