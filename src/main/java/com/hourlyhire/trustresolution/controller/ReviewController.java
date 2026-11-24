
package com.hourlyhire.trustresolution.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hourlyhire.trustresolution.dto.request.CreateReviewRequest;
import com.hourlyhire.trustresolution.dto.request.UpdateReviewRequest;
import com.hourlyhire.trustresolution.dto.response.ReviewResponse;
import com.hourlyhire.trustresolution.service.ReviewService;
import org.springframework.web.bind.annotation.PatchMapping;
import com.hourlyhire.trustresolution.dto.request.UpdateReviewStatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Create Review
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        return reviewService.createReview(request);
    }

    // Get reviews by Booking ID
    @GetMapping("/booking/{bookingId}")
    public List<ReviewResponse> getReviewsByBookingId(
            @PathVariable Long bookingId) {
        return reviewService.getReviewsByBookingId(bookingId);
    }

    // Get reviews received by a user
    @GetMapping("/user/{userId}/received")
    public List<ReviewResponse> getReviewsReceivedByUser(
            @PathVariable Long userId) {
        return reviewService.getReviewsReceivedByUser(userId);
    }

    // Update Review
    @PutMapping("/{id}")
    public ReviewResponse updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(id, request);
    }

    // Get review by ID
    @GetMapping("/{id}")
    public ReviewResponse getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    // Get all reviews
    @GetMapping
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews();
    }
 // Update Review Status
    @PatchMapping("/{id}/status")
    public ReviewResponse updateReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewStatusRequest request) {

        return reviewService.updateReviewStatus(id, request);
    }
}

