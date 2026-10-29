
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerProfileRepository
        extends JpaRepository<WorkerProfile, Long> {

    boolean existsByUserId(Long userId);

    Optional<WorkerProfile> findByUserId(Long userId);

    Page<WorkerProfile> findByProfileStatus(
            ProfileStatus profileStatus,
            Pageable pageable
    );

    Page<WorkerProfile> findByVerificationStatus(
            VerificationStatus verificationStatus,
            Pageable pageable
    );

    Page<WorkerProfile> findByDiscoverable(
            boolean discoverable,
            Pageable pageable
    );
}
