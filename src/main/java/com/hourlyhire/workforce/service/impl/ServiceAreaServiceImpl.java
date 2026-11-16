
package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.workforce.dto.request.CreateServiceAreaRequest;
import com.hourlyhire.workforce.dto.response.ServiceAreaResponse;
import com.hourlyhire.workforce.entity.ServiceArea;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.ServiceAreaRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workforce.service.ServiceAreaService;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;
import com.hourlyhire.shared.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceAreaServiceImpl implements ServiceAreaService {

    private final ServiceAreaRepository serviceAreaRepository;
    private final WorkerProfileRepository workerProfileRepository;

    // Get authenticated worker profile
    private WorkerProfile getCurrentWorkerProfile() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof AuthenticatedUser currentUser)) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Authenticated worker information not available."
            );
        }

        return workerProfileRepository.findByUserAccount_Id(currentUser.id())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "WorkerProfile",
                                String.valueOf(currentUser.id())
                        )
                );
    }

    // Convert entity into response DTO
    private ServiceAreaResponse mapToResponse(ServiceArea serviceArea) {

        return ServiceAreaResponse.builder()
                .id(serviceArea.getId())
                .workerProfileId(serviceArea.getWorkerProfile().getId())
                .city(serviceArea.getCity())
                .state(serviceArea.getState())
                .pincode(serviceArea.getPincode())
                .country(serviceArea.getCountry())
                .active(serviceArea.isActive())
                .createdAt(serviceArea.getCreatedAt())
                .updatedAt(serviceArea.getUpdatedAt())
                .build();
    }

    // Create service area
    @Override
    public ServiceAreaResponse createServiceArea(
            CreateServiceAreaRequest request) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        String city = request.getCity().trim();
        String pincode = request.getPincode().trim();

        boolean duplicate = serviceAreaRepository
                .existsByWorkerProfile_IdAndCityIgnoreCaseAndPincode(
                        workerProfile.getId(),
                        city,
                        pincode
                );

        if (duplicate) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "This service area already exists."
            );
        }

        ServiceArea serviceArea = new ServiceArea();

        serviceArea.setWorkerProfile(workerProfile);
        serviceArea.setCity(city);
        serviceArea.setState(request.getState().trim());
        serviceArea.setPincode(pincode);
        serviceArea.setCountry(request.getCountry().trim());
        serviceArea.setActive(true);

        ServiceArea savedServiceArea =
                serviceAreaRepository.save(serviceArea);

        return mapToResponse(savedServiceArea);
    }

    // Get my service areas
    @Override
    @Transactional(readOnly = true)
    public List<ServiceAreaResponse> getMyServiceAreas() {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        return serviceAreaRepository
                .findByWorkerProfile_Id(workerProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get service area by ID
    @Override
    @Transactional(readOnly = true)
    public ServiceAreaResponse getServiceAreaById(Long id) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        ServiceArea serviceArea = serviceAreaRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ServiceArea",
                                String.valueOf(id)
                        )
                );

        return mapToResponse(serviceArea);
    }

    // Update service area
    @Override
    public ServiceAreaResponse updateServiceArea(
            Long id,
            CreateServiceAreaRequest request) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        ServiceArea serviceArea = serviceAreaRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ServiceArea",
                                String.valueOf(id)
                        )
                );

        String city = request.getCity().trim();
        String pincode = request.getPincode().trim();

        boolean duplicate = serviceAreaRepository
                .existsByWorkerProfile_IdAndCityIgnoreCaseAndPincode(
                        workerProfile.getId(),
                        city,
                        pincode
                );

        if (duplicate && (
                !serviceArea.getCity().equalsIgnoreCase(city)
                        || !serviceArea.getPincode().equals(pincode))) {

            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "This service area already exists."
            );
        }

        serviceArea.setCity(city);
        serviceArea.setState(request.getState().trim());
        serviceArea.setPincode(pincode);
        serviceArea.setCountry(request.getCountry().trim());

        ServiceArea updatedServiceArea =
                serviceAreaRepository.save(serviceArea);

        return mapToResponse(updatedServiceArea);
    }

    // Deactivate service area
    @Override
    public void deactivateServiceArea(Long id) {

        WorkerProfile workerProfile = getCurrentWorkerProfile();

        ServiceArea serviceArea = serviceAreaRepository
                .findByIdAndWorkerProfile_Id(id, workerProfile.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ServiceArea",
                                String.valueOf(id)
                        )
                );

        serviceArea.setActive(false);

        serviceAreaRepository.save(serviceArea);
    }
}
