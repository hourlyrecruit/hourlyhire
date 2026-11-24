
package com.hourlyhire.trustresolution.service;

import com.hourlyhire.trustresolution.dto.request.CreateComplaintRequest;
import com.hourlyhire.trustresolution.dto.request.UpdateComplaintRequest;
import com.hourlyhire.trustresolution.dto.response.ComplaintResponse;
import com.hourlyhire.trustresolution.entity.Complaint;
import com.hourlyhire.trustresolution.mapper.ComplaintMapper;
import com.hourlyhire.trustresolution.repository.ComplaintRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ComplaintMapper complaintMapper) {
        this.complaintRepository = complaintRepository;
        this.complaintMapper = complaintMapper;
    }

    // Create Complaint
    @Transactional
    public ComplaintResponse createComplaint(CreateComplaintRequest request) {

        if (request.getComplainantUserId()
                .equals(request.getAgainstUserId())) {
            throw new IllegalArgumentException(
                    "A user cannot raise a complaint against themselves");
        }

        Complaint complaint = new Complaint();

        complaint.setBookingId(request.getBookingId());
        complaint.setComplainantUserId(request.getComplainantUserId());
        complaint.setAgainstUserId(request.getAgainstUserId());
        complaint.setSubject(request.getSubject());
        complaint.setDescription(request.getDescription());

        Complaint savedComplaint = complaintRepository.save(complaint);

        return complaintMapper.toResponse(savedComplaint);
    }

    // Get Complaint By ID
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Complaint not found with id: " + id));

        return complaintMapper.toResponse(complaint);
    }

    // Get All Complaints
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints() {

        return complaintRepository.findAll()
                .stream()
                .map(complaintMapper::toResponse)
                .toList();
    }

    // Get Complaints By Booking ID
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByBookingId(Long bookingId) {

        return complaintRepository.findByBookingId(bookingId)
                .stream()
                .map(complaintMapper::toResponse)
                .toList();
    }

    // Get Complaints By Complainant
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByComplainant(Long userId) {

        return complaintRepository.findByComplainantUserId(userId)
                .stream()
                .map(complaintMapper::toResponse)
                .toList();
    }

    // Get Complaints Against User
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsAgainstUser(Long userId) {

        return complaintRepository.findByAgainstUserId(userId)
                .stream()
                .map(complaintMapper::toResponse)
                .toList();
    }

    // Get Complaints By Status
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByStatus(String status) {

        return complaintRepository.findByStatus(status)
                .stream()
                .map(complaintMapper::toResponse)
                .toList();
    }

    // Update Complaint
    @Transactional
    public ComplaintResponse updateComplaint(
            Long id,
            UpdateComplaintRequest request) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Complaint not found with id: " + id));

        if (request.getSubject() == null
                && request.getDescription() == null) {
            throw new IllegalArgumentException(
                    "At least one field must be provided for update");
        }

        if (request.getSubject() != null) {
            if (request.getSubject().isBlank()) {
                throw new IllegalArgumentException(
                        "Subject cannot be blank");
            }
            complaint.setSubject(request.getSubject());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new IllegalArgumentException(
                        "Description cannot be blank");
            }
            complaint.setDescription(request.getDescription());
        }

        Complaint updatedComplaint =
                complaintRepository.save(complaint);

        return complaintMapper.toResponse(updatedComplaint);
    }
}
