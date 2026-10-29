package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public class WorkerProfileResponse {

    private  Long id;
    private  Long userId;
    private  String bio;
    private  String profilePhotoUrl;
    private VerificationStatus verificationStatus;
    private ProfileStatus profileStatus;
    private boolean discoverable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
