
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.AssignWorkerSkillRequest;
import com.hourlyhire.workforce.dto.response.WorkerSkillResponse;

import java.util.List;

public interface WorkerSkillService {

    // Assign a skill to the logged-in worker
    WorkerSkillResponse assignSkill(AssignWorkerSkillRequest request);

    // Get all skills assigned to the logged-in worker
    List<WorkerSkillResponse> getMySkills();

    // Update the proficiency level of an assigned skill
    WorkerSkillResponse updateSkill(
            Long workerSkillId,
            AssignWorkerSkillRequest request
    );

    // Remove a skill from the logged-in worker's profile
    void removeSkill(Long workerSkillId);
}
