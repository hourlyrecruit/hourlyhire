package com.hourlyhire.trustresolution.dto.response;

import java.time.Instant;

public class ReviewResponse {

    private Long id;
    private Long bookingId;
    private Long reviewerUserId;
    private Long revieweeUserId;
    private Integer ratingValue;
    private String comment;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public ReviewResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(Long reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public Long getRevieweeUserId() {
        return revieweeUserId;
    }

    public void setRevieweeUserId(Long revieweeUserId) {
        this.revieweeUserId = revieweeUserId;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}