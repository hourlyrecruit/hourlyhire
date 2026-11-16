
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.CreateServiceAreaRequest;
import com.hourlyhire.workforce.dto.response.ServiceAreaResponse;

import java.util.List;

public interface ServiceAreaService {

    // Add a service area for the authenticated worker
    ServiceAreaResponse createServiceArea(
            CreateServiceAreaRequest request
    );

    // Get all service areas of the authenticated worker
    List<ServiceAreaResponse> getMyServiceAreas();

    // Get a service area by ID
    ServiceAreaResponse getServiceAreaById(Long id);

    // Update an existing service area
    ServiceAreaResponse updateServiceArea(
            Long id,
            CreateServiceAreaRequest request
    );

    // Deactivate a service area
    void deactivateServiceArea(Long id);

}
