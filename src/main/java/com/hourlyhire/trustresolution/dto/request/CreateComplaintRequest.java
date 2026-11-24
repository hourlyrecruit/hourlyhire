
package com.hourlyhire.trustresolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateComplaintRequest {

    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    @NotNull(message = "Complainant user ID is required")
    @Positive(message = "Complainant user ID must be positive")
    private Long complainantUserId;

    @NotNull(message = "Against user ID is required")
    @Positive(message = "Against user ID must be positive")
    private Long againstUserId;

    @NotBlank(message = "Subject is required")
    @Size(max = 150, message = "Subject cannot exceed 150 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getComplainantUserId() {
        return complainantUserId;
    }

    public void setComplainantUserId(Long complainantUserId) {
        this.complainantUserId = complainantUserId;
    }

    public Long getAgainstUserId() {
        return againstUserId;
    }

    public void setAgainstUserId(Long againstUserId) {
        this.againstUserId = againstUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
