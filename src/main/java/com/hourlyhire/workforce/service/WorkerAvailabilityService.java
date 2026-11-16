
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.CreateAvailabilityRequest;
import com.hourlyhire.workforce.dto.response.AvailabilityResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface WorkerAvailabilityService {

    // Create a weekly or date-specific availability slot
    AvailabilityResponse createAvailability(
            CreateAvailabilityRequest request
    );

    // Get all availability slots of logged-in worker
    List<AvailabilityResponse> getMyAvailability();

    // Get weekly availability for a particular day
    List<AvailabilityResponse> getWeeklyAvailability(
            DayOfWeek dayOfWeek
    );

    // Get date-specific availability
    List<AvailabilityResponse> getAvailabilityByDate(
            LocalDate date
    );

    // Get availability record by ID
    AvailabilityResponse getAvailabilityById(Long id);

    // Update an existing availability slot
    AvailabilityResponse updateAvailability(
            Long id,
            CreateAvailabilityRequest request
    );

    // Deactivate an availability slot
    void deactivateAvailability(Long id);
}
