package com.hourlyhire.trustresolution.mapper;

import org.springframework.stereotype.Component;

import com.hourlyhire.trustresolution.dto.response.ReviewResponse;
import com.hourlyhire.trustresolution.entity.Review;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setBookingId(review.getBookingId());
        response.setReviewerUserId(review.getReviewerUserId());
        response.setRevieweeUserId(review.getRevieweeUserId());
        response.setRatingValue(review.getRatingValue());
        response.setComment(review.getComment());
        response.setStatus(review.getStatus());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());

        return response;
    }
}