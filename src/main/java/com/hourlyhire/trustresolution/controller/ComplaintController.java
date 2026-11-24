
package com.hourlyhire.trustresolution.controller;

import com.hourlyhire.trustresolution.dto.request.CreateComplaintRequest;
import com.hourlyhire.trustresolution.dto.request.UpdateComplaintRequest;
import com.hourlyhire.trustresolution.dto.response.ComplaintResponse;
import com.hourlyhire.trustresolution.service.ComplaintService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Create Complaint
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse createComplaint(
            @Valid @RequestBody CreateComplaintRequest request) {

        return complaintService.createComplaint(request);
    }

    // Get All Complaints
    @GetMapping
    public List<ComplaintResponse> getAllComplaints() {

        return complaintService.getAllComplaints();
    }

    // Get Complaint By ID
    @GetMapping("/{id}")
    public ComplaintResponse getComplaintById(
            @PathVariable Long id) {

        return complaintService.getComplaintById(id);
    }

    // Update Complaint
    @PatchMapping("/{id}")
    public ComplaintResponse updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintRequest request) {

        return complaintService.updateComplaint(id, request);
    }
}
