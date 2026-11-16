
package com.hourlyhire.workforce.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ServiceAreaResponse {

    private Long id;

    private Long workerProfileId;

    private String city;

    private String state;

    private String pincode;

    private String country;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
