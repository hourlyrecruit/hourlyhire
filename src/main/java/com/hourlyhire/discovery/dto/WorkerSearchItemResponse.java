
package com.hourlyhire.discovery.dto;

public class WorkerSearchItemResponse {

    private Long workerId;

    private String bio;

    private String profilePhotoUrl;

    private Double averageRating;

    private Long totalReviews;

    public WorkerSearchItemResponse() {
    }

    public WorkerSearchItemResponse(
            Long workerId,
            String bio,
            String profilePhotoUrl,
            Double averageRating,
            Long totalReviews) {
        this.workerId = workerId;
        this.bio = bio;
        this.profilePhotoUrl = profilePhotoUrl;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}	