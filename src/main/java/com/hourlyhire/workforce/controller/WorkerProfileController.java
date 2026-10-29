package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateWorkerProfileRequest;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.service.WorkerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
public class WorkerProfileController {
    private final WorkerProfileService workerProfileService;

    @PostMapping
    public ResponseEntity<WorkerProfileResponse> createWorkerProfile(
            @Valid @RequestBody CreateWorkerProfileRequest request) {
        WorkerProfileResponse response
                = workerProfileService.createWorkerProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workerId}")
    public ResponseEntity<WorkerProfileResponse> getWorkerProfile(@PathVariable Long workerId)
    {
        WorkerProfileResponse response = workerProfileService.getWorkerProfile(workerId);
         return ResponseEntity.ok(response);
    }



}