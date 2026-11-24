package com.hourlyhire.discovery.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hourlyhire.discovery.dto.DiscoveryResponse;
import com.hourlyhire.discovery.service.DiscoveryService;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping("/health")
    public DiscoveryResponse checkDiscoveryModule() {
        return discoveryService.checkDiscoveryStatus();
    }
}