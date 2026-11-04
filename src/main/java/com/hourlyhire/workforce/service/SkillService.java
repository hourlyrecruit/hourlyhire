
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.CreateSkillRequest;
import com.hourlyhire.workforce.dto.request.UpdateSkillRequest;
import com.hourlyhire.workforce.dto.response.SkillResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {

    // Create a new skill
    SkillResponse createSkill(CreateSkillRequest request);

    // Get skill by ID
    SkillResponse getSkillById(Long skillId);

    // Update existing skill
    SkillResponse updateSkill(Long skillId, UpdateSkillRequest request);

    // Activate or deactivate skill
    SkillResponse updateSkillStatus(Long skillId, boolean active);

    // Get all skills with pagination
    Page<SkillResponse> getAllSkills(Pageable pageable);

    // Get all active skills
    List<SkillResponse> getActiveSkills();
}
