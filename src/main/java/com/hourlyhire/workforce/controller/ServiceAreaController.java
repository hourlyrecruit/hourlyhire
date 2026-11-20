package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateServiceAreaRequest;
import com.hourlyhire.workforce.dto.response.ServiceAreaResponse;
import com.hourlyhire.workforce.service.ServiceAreaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workforce/service-areas")
@RequiredArgsConstructor
@Tag(name = "07 Worker Service Area Management",
        description = "APIs for adding, retrieving, updating and deactivating geographical service areas associated with worker profiles."
)
@SecurityRequirement(name = "bearerAuth")
public class ServiceAreaController {

    private final ServiceAreaService serviceAreaService;

    // 1. Create service area
    @PostMapping
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Add service area. ACCESS :WORKER.",
            description = "Access: WORKER. Adds a geographical service location to the authenticated worker's profile.")
    public ResponseEntity<ServiceAreaResponse> createServiceArea(
            @Valid @RequestBody CreateServiceAreaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceAreaService.createServiceArea(request));
    }

    // 2. Get my service areas
    @GetMapping("/my-areas")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Get my service areas. ACCESS :WORKER.",
            description = "Access: WORKER. Retrieves all service locations belonging to the authenticated worker.")
    public ResponseEntity<List<ServiceAreaResponse>> getMyServiceAreas() {

        return ResponseEntity.ok(
                serviceAreaService.getMyServiceAreas()
        );
    }

    // 3. Get service area by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Get service area by ID. ACCESS :WORKER.",
            description = "Access: WORKER. Retrieves a service area belonging to the authenticated worker using its ID.")
    public ResponseEntity<ServiceAreaResponse> getServiceAreaById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                serviceAreaService.getServiceAreaById(id)
        );
    }

    // 4. Update service area
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Update service area. ACCESS :WORKER.",
            description = "Access: WORKER. Updates the city, state, pincode and country of the worker's service area.")
    public ResponseEntity<ServiceAreaResponse> updateServiceArea(
            @PathVariable Long id,
            @Valid @RequestBody CreateServiceAreaRequest request) {

        return ResponseEntity.ok(
                serviceAreaService.updateServiceArea(id, request)
        );
    }

    // 5. Deactivate service area
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Deactivate service area. ACCESS :WORKER.",
            description = "Access: WORKER. Deactivates a service area belonging to the authenticated worker without permanently deleting the record.")
    public ResponseEntity<Void> deactivateServiceArea(
            @PathVariable Long id) {

        serviceAreaService.deactivateServiceArea(id);

        return ResponseEntity.noContent().build();
    }
}
