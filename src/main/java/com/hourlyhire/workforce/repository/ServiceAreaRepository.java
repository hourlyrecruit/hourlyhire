
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.ServiceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAreaRepository
        extends JpaRepository<ServiceArea, Long> {

    // Get all service areas belonging to a worker profile
    List<ServiceArea> findByWorkerProfile_Id(Long workerProfileId);

    // Get only active service areas of a worker
    List<ServiceArea> findByWorkerProfile_IdAndActiveTrue(
            Long workerProfileId
    );

    // Find a service area by ID and worker profile ID
    Optional<ServiceArea> findByIdAndWorkerProfile_Id(
            Long id,
            Long workerProfileId
    );

    // Check whether a worker already has this city and pincode
    boolean existsByWorkerProfile_IdAndCityIgnoreCaseAndPincode(
            Long workerProfileId,
            String city,
            String pincode
    );
}
