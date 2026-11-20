
package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.AssignWorkerSkillRequest;
import com.hourlyhire.workforce.dto.response.WorkerSkillResponse;
import com.hourlyhire.workforce.service.WorkerSkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce/worker-skills")
@Tag(name = "06 Worker Skill Management",
        description = "APIs for managing worker skills, including adding, retrieving, updating and removing skills associated with worker profiles.")
@RequiredArgsConstructor
public class WorkerSkillController {

    private final WorkerSkillService workerSkillService;

    // 1. Assign a skill to the logged-in worker
    @Operation(summary = "Assign a skill to a worker. Access : WORKER,ADMIN",
            description = "Access: ADMIN, WORKER. Assigns a skill and proficiency level to a worker profile.")
    @PostMapping
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    public ResponseEntity<WorkerSkillResponse> assignSkill(
            @Valid @RequestBody AssignWorkerSkillRequest request) {

        WorkerSkillResponse response =
                workerSkillService.assignSkill(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all skills of the logged-in worker
    @Operation(summary = "Get my skills. Access : WORKER.",
            description = "Access: WORKER. Retrieves all skills assigned to the currently authenticated worker.")
    @GetMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<WorkerSkillResponse>> getMySkills() {

        return ResponseEntity.ok(
                workerSkillService.getMySkills()
        );
    }

    // 3. Update an assigned skill
    @Operation(summary = "Update worker skill. Access : WORKER,ADMIN.",
            description = "Access: ADMIN, WORKER. Updates the proficiency level of an assigned worker skill."
    )
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @PutMapping("/{workerSkillId}")
    public ResponseEntity<WorkerSkillResponse> updateSkill(
            @PathVariable Long workerSkillId,
            @Valid @RequestBody AssignWorkerSkillRequest request) {

        return ResponseEntity.ok(
                workerSkillService.updateSkill(workerSkillId, request)
        );
    }

    // 4. Remove an assigned skill
    @Operation(summary = "Remove worker skill. Access : WORKER,ADMIN.",
            description = "Access: ADMIN, WORKER. Removes a skill assignment from a worker profile.")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @DeleteMapping("/{workerSkillId}")
    public ResponseEntity<Void> removeSkill(
            @PathVariable Long workerSkillId) {

        workerSkillService.removeSkill(workerSkillId);

        return ResponseEntity.noContent().build();
    }
}
