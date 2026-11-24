
package com.hourlyhire.discovery.dto;

import java.util.List;

public class WorkerSearchResponse {

    private List<WorkerSearchItemResponse> workers;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    public WorkerSearchResponse() {
    }

    public WorkerSearchResponse(
            List<WorkerSearchItemResponse> workers,
            int page,
            int size,
            long totalElements,
            int totalPages) {

        this.workers = workers;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<WorkerSearchItemResponse> getWorkers() {
        return workers;
    }

    public void setWorkers(List<WorkerSearchItemResponse> workers) {
        this.workers = workers;
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

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
