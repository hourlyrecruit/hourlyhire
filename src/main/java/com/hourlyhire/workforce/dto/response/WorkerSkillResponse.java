package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.workforce.enums.ProficiencyLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkerSkillResponse {

    private Long id;

    private Long workerProfileId;

    private Long skillId;

    private String skillName;

    private String skillDescription;

    private ProficiencyLevel proficiencyLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}