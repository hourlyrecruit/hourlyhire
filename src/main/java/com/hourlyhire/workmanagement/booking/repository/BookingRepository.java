
package com.hourlyhire.workmanagement.booking.repository;

import com.hourlyhire.workmanagement.booking.dto.response.BookingResponse;
import com.hourlyhire.workmanagement.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByEmployerProfile_Id(Long employerProfileId);
    List<Booking> findAllByWorkerProfile_Id(Long workerProfileId);
    Optional<Booking> findByIdAndEmployerProfile_Id(
            Long bookingId,
            Long employerProfileId
    );

}
