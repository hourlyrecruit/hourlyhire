
package com.hourlyhire.workmanagement.booking.repository;

import com.hourlyhire.workmanagement.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByEmployerProfile_Id(Long employerProfileId);

}
