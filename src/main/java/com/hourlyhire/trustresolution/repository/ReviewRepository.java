
package com.hourlyhire.trustresolution.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hourlyhire.trustresolution.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get all reviews by Booking ID
    List<Review> findByBookingId(Long bookingId);

    // Get all reviews received by a user
    List<Review> findByRevieweeUserId(Long revieweeUserId);

}
