
package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.workforce.enums.VerificationStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerPublicProfileResponse {

    private Long id;

    private String username;

    private String bio;

    private String profilePhotoUrl;

    private VerificationStatus verificationStatus;
}
