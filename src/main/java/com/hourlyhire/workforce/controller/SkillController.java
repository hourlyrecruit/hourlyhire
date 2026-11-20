package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateSkillRequest;
import com.hourlyhire.workforce.dto.request.UpdateSkillRequest;
import com.hourlyhire.workforce.dto.response.SkillResponse;
import com.hourlyhire.workforce.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce/skills")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "05 Skill Management", description = "APIs for creating, retrieving, updating, and managing skills")
public class SkillController {

    private final SkillService skillService;

    // 1. Create a new skill
    @PostMapping
    @Operation(summary = "Create a new skill. Access:ADMIN",
            description = "Allows an administrator to create a new skill.")
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest request) {

        SkillResponse response = skillService.createSkill(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get skill by ID
    @GetMapping("/{skillId}")
    @Operation(summary = "Get skill by ID. Access:ADMIN",
            description = "Retrieves the details of a skill using its skill ID.")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillService.getSkillById(skillId)
        );
    }

    // 3. Get all skills with pagination
    @GetMapping
    @Operation(summary = "Get all skills. Access:ADMIN",
            description = "Retrieves all skills with pagination and sorting support.")
    public ResponseEntity<Page<SkillResponse>> getAllSkills(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "name",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                skillService.getAllSkills(pageable)
        );
    }

    // 4. Get active skills
    @GetMapping("/active")
    @Operation(summary = "Get active skills. Access:ADMIN",
            description = "Retrieves the list of currently active skills.")
    public ResponseEntity<List<SkillResponse>> getActiveSkills() {

        return ResponseEntity.ok(
                skillService.getActiveSkills()
        );
    }

    // 5. Update skill
    @PutMapping("/{skillId}")
    @Operation(summary = "Update skill. Access:ADMIN.",
            description = "Updates the details of an existing skill using its skill ID.")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillRequest request) {

        return ResponseEntity.ok(
                skillService.updateSkill(skillId, request)
        );
    }

    // 6. Activate or deactivate skill
    @PatchMapping("/{skillId}/status")
    @Operation(summary = "Update skill status. Access:ADMIN",
            description = "Activates or deactivates a skill using its skill ID and active status.")
    public ResponseEntity<SkillResponse> updateSkillStatus(
            @PathVariable Long skillId,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                skillService.updateSkillStatus(skillId, active)
        );
    }
}
