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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce/skills")
@RequiredArgsConstructor
@Tag(name = "Skill Management", description = "Manage available worker skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @Operation( summary = "Create skill",
            description = "Creates a new skill in the skill catalog."
    )
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest request) {

        SkillResponse response = skillService.createSkill(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{skillId}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillService.getSkillById(skillId)
        );
    }


    @PutMapping("/{skillId}")
    @Operation(summary = "Update skill details")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long skillId,
            @Valid @RequestBody UpdateSkillRequest request) {

        return ResponseEntity.ok(
                skillService.updateSkill(skillId, request)
        );
    }

    @PatchMapping("/{skillId}/status")
    @Operation(summary = "Update skill active status")
    public ResponseEntity<SkillResponse> updateSkillStatus(
            @PathVariable Long skillId,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                skillService.updateSkillStatus(skillId, active)
        );
    }

    @GetMapping
    @Operation(summary = "Get all skills")
    public ResponseEntity<Page<SkillResponse>> getAllSkills(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable) {

        return ResponseEntity.ok(
                skillService.getAllSkills(pageable)
        );
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active skills")
    public ResponseEntity<List<SkillResponse>> getActiveSkills() {

        return ResponseEntity.ok(
                skillService.getActiveSkills()
        );
    }
}
