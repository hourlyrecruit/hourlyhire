package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateSkillRequest;
import com.hourlyhire.workforce.dto.request.UpdateSkillRequest;
import com.hourlyhire.workforce.dto.response.SkillResponse;
import com.hourlyhire.workforce.service.SkillService;

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
public class SkillController {

    private final SkillService skillService;

    // 1. Create a new skill
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest request) {

        SkillResponse response = skillService.createSkill(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get skill by ID
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillService.getSkillById(skillId)
        );
    }

    // 3. Get all skills with pagination
    @GetMapping
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
    public ResponseEntity<List<SkillResponse>> getActiveSkills() {

        return ResponseEntity.ok(
                skillService.getActiveSkills()
        );
    }

    // 5. Update skill
    @PutMapping("/{skillId}")
  //  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillRequest request) {

        return ResponseEntity.ok(
                skillService.updateSkill(skillId, request)
        );
    }

    // 6. Activate or deactivate skill
    @PatchMapping("/{skillId}/status")
  //  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> updateSkillStatus(
            @PathVariable Long skillId,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                skillService.updateSkillStatus(skillId, active)
        );
    }
}
