
package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.workforce.dto.request.CreateSkillRequest;
import com.hourlyhire.workforce.dto.request.UpdateSkillRequest;
import com.hourlyhire.workforce.dto.response.SkillResponse;
import com.hourlyhire.workforce.entity.Skill;
import com.hourlyhire.workforce.repository.SkillRepository;
import com.hourlyhire.workforce.service.SkillService;

import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;
import com.hourlyhire.shared.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public SkillResponse createSkill(CreateSkillRequest request) {

        String skillName = request.getName().trim();

        if (skillRepository.existsByNameIgnoreCase(skillName)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Skill already exists: " + skillName
            );
        }

        Skill skill = new Skill();

        skill.setName(skillName);
        skill.setDescription(request.getDescription());
        skill.setActive(true);

        Skill savedSkill = skillRepository.save(skill);

        return mapToResponse(savedSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long skillId) {

        Skill skill = findSkillById(skillId);

        return mapToResponse(skill);
    }

    @Override
    public SkillResponse updateSkill(
            Long skillId,
            UpdateSkillRequest request) {

        Skill skill = findSkillById(skillId);

        if (request.getName() != null) {

            String skillName = request.getName().trim();

            if (skillName.isBlank()) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "Skill name cannot be blank."
                );
            }

            if (skillRepository.existsByNameIgnoreCaseAndIdNot(
                    skillName, skillId)) {

                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "Another skill already exists with name: " + skillName
                );
            }

            skill.setName(skillName);
        }

        if (request.getDescription() != null) {
            skill.setDescription(request.getDescription());
        }

        Skill updatedSkill = skillRepository.save(skill);

        return mapToResponse(updatedSkill);
    }


    @Override
    public SkillResponse updateSkillStatus(
            Long skillId,
            boolean active) {

        Skill skill = findSkillById(skillId);

        skill.setActive(active);

        Skill updatedSkill = skillRepository.save(skill);

        return mapToResponse(updatedSkill);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SkillResponse> getAllSkills(Pageable pageable) {

        return skillRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getActiveSkills() {

        return skillRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private Skill findSkillById(Long skillId) {

        return skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Skill",
                        skillId.toString()
                ));
    }

    // Convert entity into response DTO
    private SkillResponse mapToResponse(Skill skill) {

        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .active(skill.isActive())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }
}
