
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

    @Query("""
    SELECT p FROM WorkerProfile p
    WHERE (:profileStatus IS NULL OR p.profileStatus = :profileStatus)
    AND (:verificationStatus IS NULL OR p.verificationStatus = :verificationStatus)
    AND (:discoverable IS NULL OR p.discoverable = :discoverable)
    """)
    Page<WorkerProfile> findByFilters(
            @Param("profileStatus") ProfileStatus profileStatus,
            @Param("verificationStatus") VerificationStatus verificationStatus,
            @Param("discoverable") Boolean discoverable,
            Pageable pageable
    );
}
