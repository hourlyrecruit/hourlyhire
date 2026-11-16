
package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;
import com.hourlyhire.workforce.dto.request.CreateAvailabilityRequest;
import com.hourlyhire.workforce.dto.response.AvailabilityResponse;
import com.hourlyhire.workforce.entity.WorkerAvailability;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.enums.AvailabilityType;
import com.hourlyhire.workforce.repository.WorkerAvailabilityRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workforce.service.WorkerAvailabilityService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerAvailabilityServiceImpl
        implements WorkerAvailabilityService {

    private final WorkerAvailabilityRepository availabilityRepository;
    private final WorkerProfileRepository workerProfileRepository;

    // Get authenticated worker profile
    private WorkerProfile getCurrentWorkerProfile() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof AuthenticatedUser currentUser)
                || currentUser.role() != com.hourlyhire.authentication.enums.Role.WORKER) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Only workers can manage their availability."
            );
        }

        return workerProfileRepository
                .findByUserAccount_Id(currentUser.id())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerProfile",
                                String.valueOf(currentUser.id())
                        )
                );
    }

    // Validate availability request
    private void validateRequest(CreateAvailabilityRequest request) {

        if (request.getStartTime() == null
                || request.getEndTime() == null
                || !request.getStartTime().isBefore(request.getEndTime())) {

            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Start time must be earlier than end time."
            );
        }

        if (request.getAvailabilityType() == null) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Availability type is required."
            );
        }

        if (request.getAvailabilityType() == AvailabilityType.WEEKLY) {

            if (request.getDayOfWeek() == null
                    || request.getAvailableDate() != null) {

                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "Weekly availability requires a day of week only."
                );
            }
        }

        if (request.getAvailabilityType() == AvailabilityType.DATE_SPECIFIC) {

            if (request.getAvailableDate() == null
                    || request.getDayOfWeek() != null) {

                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "Date-specific availability requires a date only."
                );
            }

            if (request.getAvailableDate().isBefore(LocalDate.now())) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "Past dates are not allowed."
                );
            }
        }
    }

    // Check overlapping weekly or date-specific slots
    private void validateNoOverlap(
            WorkerProfile workerProfile,
            CreateAvailabilityRequest request,
            Long excludedId) {

        List<WorkerAvailability> existingSlots;

        if (request.getAvailabilityType() == AvailabilityType.WEEKLY) {

            existingSlots = availabilityRepository
                    .findByWorkerProfile_IdAndAvailabilityTypeAndDayOfWeekAndAvailableTrueAndStartTimeLessThanAndEndTimeGreaterThan(
                            workerProfile.getId(),
                            AvailabilityType.WEEKLY,
                            request.getDayOfWeek(),
                            request.getEndTime(),
                            request.getStartTime()
                    );

        } else {

            existingSlots = availabilityRepository
                    .findByWorkerProfile_IdAndAvailabilityTypeAndAvailableDateAndAvailableTrueAndStartTimeLessThanAndEndTimeGreaterThan(
                            workerProfile.getId(),
                            AvailabilityType.DATE_SPECIFIC,
                            request.getAvailableDate(),
                            request.getEndTime(),
                            request.getStartTime()
                    );
        }

        boolean overlap = existingSlots.stream()
                .anyMatch(slot -> !slot.getId().equals(excludedId));

        if (overlap) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "This time slot overlaps with an existing availability slot."
            );
        }
    }

    // Convert entity to response
    private AvailabilityResponse mapToResponse(
            WorkerAvailability availability) {

        return AvailabilityResponse.builder()
                .id(availability.getId())
                .workerProfileId(availability.getWorkerProfile().getId())
                .availabilityType(availability.getAvailabilityType())
                .dayOfWeek(availability.getDayOfWeek())
                .availableDate(availability.getAvailableDate())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .available(availability.isAvailable())
                .createdAt(availability.getCreatedAt())
                .updatedAt(availability.getUpdatedAt())
                .build();
    }

    // Create availability
    @Override
    public AvailabilityResponse createAvailability(
            CreateAvailabilityRequest request) {

        validateRequest(request);

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        if (Boolean.TRUE.equals(request.getAvailable())) {
            validateNoOverlap(workerProfile, request, null);
        }

        WorkerAvailability availability = new WorkerAvailability();

        availability.setWorkerProfile(workerProfile);
        availability.setAvailabilityType(request.getAvailabilityType());
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setAvailable(
                request.getAvailable() == null || request.getAvailable()
        );

        return mapToResponse(
                availabilityRepository.save(availability)
        );
    }

    // Get all availability
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getMyAvailability() {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        return availabilityRepository
                .findByWorkerProfile_Id(workerProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get weekly availability
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getWeeklyAvailability(
            DayOfWeek dayOfWeek) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        return availabilityRepository
                .findByWorkerProfile_IdAndAvailabilityTypeAndDayOfWeek(
                        workerProfile.getId(),
                        AvailabilityType.WEEKLY,
                        dayOfWeek
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get date-specific availability
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilityByDate(
            LocalDate date) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        return availabilityRepository
                .findByWorkerProfile_IdAndAvailabilityTypeAndAvailableDate(
                        workerProfile.getId(),
                        AvailabilityType.DATE_SPECIFIC,
                        date
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get availability by ID
    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailabilityById(Long id) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        WorkerAvailability availability = availabilityRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerAvailability",
                                String.valueOf(id)
                        )
                );

        return mapToResponse(availability);
    }

    // Update availability
    @Override
    public AvailabilityResponse updateAvailability(
            Long id,
            CreateAvailabilityRequest request) {

        validateRequest(request);

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        WorkerAvailability availability = availabilityRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerAvailability",
                                String.valueOf(id)
                        )
                );

        if (Boolean.TRUE.equals(request.getAvailable())) {
            validateNoOverlap(workerProfile, request, id);
        }

        availability.setAvailabilityType(request.getAvailabilityType());
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setAvailableDate(request.getAvailableDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setAvailable(
                request.getAvailable() == null || request.getAvailable()
        );

        return mapToResponse(
                availabilityRepository.save(availability)
        );
    }

    // Deactivate availability
    @Override
    public void deactivateAvailability(Long id) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        WorkerAvailability availability = availabilityRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerAvailability",
                                String.valueOf(id)
                        )
                );

        availability.setAvailable(false);
        availability.setUpdatedAt(LocalDateTime.now());

        availabilityRepository.save(availability);
    }
}
