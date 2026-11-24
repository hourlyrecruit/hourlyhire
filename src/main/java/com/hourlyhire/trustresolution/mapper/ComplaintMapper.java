
package com.hourlyhire.trustresolution.mapper;

import com.hourlyhire.trustresolution.dto.response.ComplaintResponse;
import com.hourlyhire.trustresolution.entity.Complaint;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {

    public ComplaintResponse toResponse(Complaint complaint) {

        ComplaintResponse response = new ComplaintResponse();

        response.setId(complaint.getId());
        response.setBookingId(complaint.getBookingId());
        response.setComplainantUserId(complaint.getComplainantUserId());
        response.setAgainstUserId(complaint.getAgainstUserId());
        response.setSubject(complaint.getSubject());
        response.setDescription(complaint.getDescription());
        response.setStatus(complaint.getStatus());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setUpdatedAt(complaint.getUpdatedAt());
        response.setVersion(complaint.getVersion());

        return response;
    }
}
