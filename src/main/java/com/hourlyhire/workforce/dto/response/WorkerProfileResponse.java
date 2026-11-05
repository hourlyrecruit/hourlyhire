package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
@Getter
@Builder
public class WorkerProfileResponse {

    private Long id;
    private Long userId;

    private String username;
    private String email;
    private Role role;

    private String bio;
    private String profilePhotoUrl;

    private VerificationStatus verificationStatus;
    private ProfileStatus profileStatus;

    private Boolean discoverable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
