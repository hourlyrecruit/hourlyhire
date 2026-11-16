
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerProfileRepository
        extends JpaRepository<WorkerProfile, Long> {

    // Check whether a worker profile already exists for a user
    boolean existsByUserAccount_Id(Long userId);

    // Find worker profile using UserAccount ID
    Optional<WorkerProfile> findByUserAccount_Id(Long userId);

    // Find profiles by profile status
    Page<WorkerProfile> findByProfileStatus(
            ProfileStatus profileStatus,
            Pageable pageable
    );

    // Find profiles by verification status
    Page<WorkerProfile> findByVerificationStatus(
            VerificationStatus verificationStatus,
            Pageable pageable
    );

    // Find profiles by discoverability
    Page<WorkerProfile> findByDiscoverable(
            boolean discoverable,
            Pageable pageable
    );

    // Find profiles using multiple optional filters
    @Query("""
        SELECT p FROM WorkerProfile p
        WHERE (:profileStatus IS NULL
               OR p.profileStatus = :profileStatus)
        AND (:verificationStatus IS NULL
             OR p.verificationStatus = :verificationStatus)
        AND (:discoverable IS NULL
             OR p.discoverable = :discoverable)
        """)
    Page<WorkerProfile> findByFilters(
            @Param("profileStatus") ProfileStatus profileStatus,
            @Param("verificationStatus") VerificationStatus verificationStatus,
            @Param("discoverable") Boolean discoverable,
            Pageable pageable
    );
}
