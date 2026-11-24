package com.hourlyhire.discovery.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class WorkerSearchRequest {

    private Long skillId;

    private Long serviceAreaId;

    private OffsetDateTime availableFrom;

    private OffsetDateTime availableTo;

    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 20;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }

    public OffsetDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(OffsetDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public OffsetDateTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(OffsetDateTime availableTo) {
        this.availableTo = availableTo;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}