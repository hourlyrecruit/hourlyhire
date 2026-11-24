package com.hourlyhire.discovery.controller;

import com.hourlyhire.discovery.dto.WorkerSearchRequest;
import com.hourlyhire.discovery.dto.WorkerSearchResponse;
import com.hourlyhire.discovery.service.WorkerDiscoveryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/discovery")
public class WorkerDiscoveryController {

    private final WorkerDiscoveryService workerDiscoveryService;

    public WorkerDiscoveryController(
            WorkerDiscoveryService workerDiscoveryService) {
        this.workerDiscoveryService = workerDiscoveryService;
    }

    @GetMapping("/workers")
    public WorkerSearchResponse searchWorkers(
            @Valid @ModelAttribute WorkerSearchRequest request) {

        return workerDiscoveryService.searchWorkers(request);
    }
}