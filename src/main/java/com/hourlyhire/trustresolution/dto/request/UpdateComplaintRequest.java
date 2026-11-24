
package com.hourlyhire.trustresolution.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateComplaintRequest {

    @Size(max = 150, message = "Subject cannot exceed 150 characters")
    private String subject;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

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
