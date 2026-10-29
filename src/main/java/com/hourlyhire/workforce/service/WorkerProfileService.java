package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.CreateWorkerProfileRequest;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;

public interface WorkerProfileService {

    WorkerProfileResponse createWorkerProfile(CreateWorkerProfileRequest request);

    WorkerProfileResponse getWorkerProfile(Long workerId);

    boolean existsByUserId(Long userId);


}
