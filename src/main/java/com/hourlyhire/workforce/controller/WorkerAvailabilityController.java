
package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateAvailabilityRequest;
import com.hourlyhire.workforce.dto.response.AvailabilityResponse;
import com.hourlyhire.workforce.service.WorkerAvailabilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce/availability")
@RequiredArgsConstructor
@Tag(
        name = "Worker Availability",
        description = "Manage weekly and date-specific worker availability"
)
@PreAuthorize("hasRole('WORKER')")
public class WorkerAvailabilityController {

    private final WorkerAvailabilityService availabilityService;

    // Create availability
    @PostMapping
    @Operation(
            summary = "Create worker availability",
            description = "Access: WORKER. Creates a weekly or date-specific availability slot for the authenticated worker."
    )
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.createAvailability(request));
    }

    // Get all availability
    @GetMapping("/my-availability")
    @Operation(
            summary = "Get my availability",
            description = "Access: WORKER. Retrieves all weekly and date-specific availability slots of the authenticated worker."
    )
    public ResponseEntity<List<AvailabilityResponse>> getMyAvailability() {

        return ResponseEntity.ok(
                availabilityService.getMyAvailability()
        );
    }

    // Get weekly availability by day
    @GetMapping("/weekly/{dayOfWeek}")
    @Operation(
            summary = "Get weekly availability by day",
            description = "Access: WORKER. Retrieves all availability time slots configured for a particular weekday."
    )
    public ResponseEntity<List<AvailabilityResponse>> getWeeklyAvailability(
            @PathVariable DayOfWeek dayOfWeek) {

        return ResponseEntity.ok(
                availabilityService.getWeeklyAvailability(dayOfWeek)
        );
    }

    // Get date-specific availability
    @GetMapping("/date")
    @Operation(
            summary = "Get availability by date",
            description = "Access: WORKER. Retrieves all availability time slots configured for a specific calendar date."
    )
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                availabilityService.getAvailabilityByDate(date)
        );
    }

    // Get availability by ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Get availability by ID",
            description = "Access: WORKER. Retrieves an availability record belonging to the authenticated worker."
    )
    public ResponseEntity<AvailabilityResponse> getAvailabilityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                availabilityService.getAvailabilityById(id)
        );
    }

    // Update availability
    @PutMapping("/{id}")
    @Operation(
            summary = "Update worker availability",
            description = "Access: WORKER. Updates an existing weekly or date-specific availability time slot."
    )
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody CreateAvailabilityRequest request) {

        return ResponseEntity.ok(
                availabilityService.updateAvailability(id, request)
        );
    }

    // Deactivate availability
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deactivate worker availability",
            description = "Access: WORKER. Marks an availability slot as unavailable without permanently deleting the record."
    )
    public ResponseEntity<Void> deactivateAvailability(
            @PathVariable Long id) {

        availabilityService.deactivateAvailability(id);

        return ResponseEntity.noContent().build();
    }
}
