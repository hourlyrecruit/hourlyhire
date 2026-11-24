package com.hourlyhire.trustresolution.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hourlyhire.trustresolution.dto.request.CreateReviewRequest;
import com.hourlyhire.trustresolution.dto.request.UpdateReviewRequest;
import com.hourlyhire.trustresolution.dto.response.ReviewResponse;
import com.hourlyhire.trustresolution.entity.Review;
import com.hourlyhire.trustresolution.mapper.ReviewMapper;
import com.hourlyhire.trustresolution.repository.ReviewRepository;
import com.hourlyhire.trustresolution.dto.request.UpdateReviewStatusRequest;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    public ReviewResponse createReview(CreateReviewRequest request) {

        Review review = new Review();

        review.setBookingId(request.getBookingId());
        review.setRevieweeUserId(request.getRevieweeUserId());
        review.setRatingValue(request.getRatingValue());
        review.setComment(request.getComment());

        // Temporary placeholder until authentication is integrated.
        review.setReviewerUserId(1L);
        review.setStatus("ACTIVE");

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Review not found with id: " + id));

        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {

        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    // Get reviews by Booking ID
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBookingId(Long bookingId) {

        return reviewRepository.findByBookingId(bookingId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    // Get reviews received by a user
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsReceivedByUser(Long userId) {

        return reviewRepository.findByRevieweeUserId(userId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    // Update Review
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Review not found with id: " + id));

        review.setRatingValue(request.getRatingValue());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(updatedReview);
    }
 // Update Review Status
    public ReviewResponse updateReviewStatus(
            Long id,
            UpdateReviewStatusRequest request) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Review not found with id: " + id));

        review.setStatus(request.getStatus());

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(updatedReview);
    }
}