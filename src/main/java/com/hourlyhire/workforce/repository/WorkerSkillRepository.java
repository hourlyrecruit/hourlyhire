
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.WorkerSkill;
import com.hourlyhire.workforce.enums.ProficiencyLevel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerSkillRepository
        extends JpaRepository<WorkerSkill, Long> {

    List<WorkerSkill> findByWorkerProfile_Id(Long workerProfileId);

    List<WorkerSkill> findBySkill_Id(Long skillId);

    Optional<WorkerSkill> findByWorkerProfile_IdAndSkill_Id(
            Long workerProfileId,
            Long skillId
    );

    boolean existsByWorkerProfile_IdAndSkill_Id(
            Long workerProfileId,
            Long skillId
    );

    void deleteByWorkerProfile_IdAndSkill_Id(
            Long workerProfileId,
            Long skillId
    );

    List<WorkerSkill> findByProficiencyLevel(
            ProficiencyLevel proficiencyLevel
    );
}
