package com.hourlyhire.discovery.serviceImpl;

import org.springframework.stereotype.Service;

import com.hourlyhire.discovery.dto.DiscoveryResponse;
import com.hourlyhire.discovery.service.DiscoveryService;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Override
    public DiscoveryResponse checkDiscoveryStatus() {
        return new DiscoveryResponse(
                "HourlyHire Discovery Module is running",
                true
        );
    }
}