package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;

import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;

import com.hourlyhire.workforce.dto.request.AssignWorkerSkillRequest;
import com.hourlyhire.workforce.dto.response.WorkerSkillResponse;

import com.hourlyhire.workforce.entity.Skill;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.entity.WorkerSkill;

import com.hourlyhire.workforce.repository.SkillRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workforce.repository.WorkerSkillRepository;

import com.hourlyhire.workforce.service.WorkerSkillService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerSkillServiceImpl implements WorkerSkillService {

    private final WorkerSkillRepository workerSkillRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final SkillRepository skillRepository;

    // 1. Assign a skill to the logged-in worker
    @Override
    public WorkerSkillResponse assignSkill(AssignWorkerSkillRequest request) {

        AuthenticatedUser currentUser = getCurrentWorker();

        WorkerProfile workerProfile =
                findWorkerProfile(currentUser.id());

        Skill skill = findActiveSkill(request.getSkillId());

        boolean alreadyAssigned =
                workerSkillRepository.existsByWorkerProfile_IdAndSkill_Id(
                        workerProfile.getId(),
                        skill.getId()
                );

        if (alreadyAssigned) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "This skill is already assigned to your profile."
            );
        }

        WorkerSkill workerSkill = new WorkerSkill();

        workerSkill.setWorkerProfile(workerProfile);
        workerSkill.setSkill(skill);
        workerSkill.setProficiencyLevel(request.getProficiencyLevel());

        WorkerSkill savedSkill =
                workerSkillRepository.save(workerSkill);

        return mapToResponse(savedSkill);
    }

    // 2. Get all skills assigned to the logged-in worker
    @Override
    @Transactional(readOnly = true)
    public List<WorkerSkillResponse> getMySkills() {

        AuthenticatedUser currentUser = getCurrentWorker();

        WorkerProfile workerProfile =
                findWorkerProfile(currentUser.id());

        return workerSkillRepository
                .findByWorkerProfile_Id(workerProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Update an assigned skill and its proficiency
    @Override
    public WorkerSkillResponse updateSkill(
            Long workerSkillId,
            AssignWorkerSkillRequest request) {

        AuthenticatedUser currentUser = getCurrentWorker();

        WorkerProfile workerProfile =
                findWorkerProfile(currentUser.id());

        WorkerSkill workerSkill =
                workerSkillRepository.findById(workerSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "WorkerSkill",
                                        workerSkillId.toString()
                                )
                        );

        // Verify that the assignment belongs to this worker
        if (!workerSkill.getWorkerProfile().getId()
                .equals(workerProfile.getId())) {

            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "You cannot update another worker's skill."
            );
        }

        Skill newSkill = findActiveSkill(request.getSkillId());

        // Prevent duplicate skill assignments
        workerSkillRepository
                .findByWorkerProfile_IdAndSkill_Id(
                        workerProfile.getId(),
                        newSkill.getId()
                )
                .ifPresent(existing -> {
                    if (!existing.getId().equals(workerSkillId)) {
                        throw new BusinessException(
                                ErrorCode.VALIDATION_FAILED,
                                "This skill is already assigned to your profile."
                        );
                    }
                });

        workerSkill.setSkill(newSkill);
        workerSkill.setProficiencyLevel(request.getProficiencyLevel());

        WorkerSkill updatedSkill =
                workerSkillRepository.save(workerSkill);

        return mapToResponse(updatedSkill);
    }

    // 4. Remove a skill from the logged-in worker's profile
    @Override
    public void removeSkill(Long workerSkillId) {

        AuthenticatedUser currentUser = getCurrentWorker();

        WorkerProfile workerProfile =
                findWorkerProfile(currentUser.id());

        WorkerSkill workerSkill =
                workerSkillRepository.findById(workerSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "WorkerSkill",
                                        workerSkillId.toString()
                                )
                        );

        // Verify assignment ownership
        if (!workerSkill.getWorkerProfile().getId()
                .equals(workerProfile.getId())) {

            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "You cannot remove another worker's skill."
            );
        }

        workerSkillRepository.delete(workerSkill);
    }

    // Get the authenticated worker
    private AuthenticatedUser getCurrentWorker() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {

            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Authenticated worker information is unavailable."
            );
        }

        AuthenticatedUser currentUser =
                (AuthenticatedUser) authentication.getPrincipal();

        if (currentUser.role() != Role.WORKER) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Only workers can manage their skill assignments."
            );
        }

        return currentUser;
    }

    // Find worker profile using authenticated user ID
    private WorkerProfile findWorkerProfile(Long userId) {

        return workerProfileRepository.findByUserAccount_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerProfile",
                                userId.toString()
                        )
                );
    }

    // Find skill and check whether it is active
    private Skill findActiveSkill(Long skillId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Skill",
                                skillId.toString()
                        )
                );

        if (!skill.isActive()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Selected skill is inactive."
            );
        }

        return skill;
    }

    // Convert entity into response DTO
    private WorkerSkillResponse mapToResponse(WorkerSkill workerSkill) {

        return WorkerSkillResponse.builder()
                .id(workerSkill.getId())
                .workerProfileId(workerSkill.getWorkerProfile().getId())
                .skillId(workerSkill.getSkill().getId())
                .skillName(workerSkill.getSkill().getName())
                .proficiencyLevel(workerSkill.getProficiencyLevel())
                .build();
    }
}
