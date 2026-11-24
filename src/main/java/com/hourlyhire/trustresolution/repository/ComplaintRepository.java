
package com.hourlyhire.trustresolution.repository;

import com.hourlyhire.trustresolution.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByBookingId(Long bookingId);

    List<Complaint> findByComplainantUserId(Long complainantUserId);

    List<Complaint> findByAgainstUserId(Long againstUserId);

    List<Complaint> findByStatus(String status);
}
