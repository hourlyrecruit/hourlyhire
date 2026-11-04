
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployerProfileRepository
        extends JpaRepository<EmployerProfile, Long> {

    // Check whether an employer profile already exists for a user
    boolean existsByUserId(Long userId);

    // Find employer profile using user ID
    Optional<EmployerProfile> findByUserId(Long userId);

    // Find profiles using employer type
    Page<EmployerProfile> findByEmployerType(
            EmployerType employerType,
            Pageable pageable
    );

    // Find profiles using profile status
    Page<EmployerProfile> findByProfileStatus(
            ProfileStatus profileStatus,
            Pageable pageable
    );

    // Find profiles using employer type and status
    Page<EmployerProfile> findByEmployerTypeAndProfileStatus(
            EmployerType employerType,
            ProfileStatus profileStatus,
            Pageable pageable
    );

    // Find profiles using optional filters
    @Query("""
        SELECT e FROM EmployerProfile e
        WHERE (:employerType IS NULL
               OR e.employerType = :employerType)
        AND (:profileStatus IS NULL
               OR e.profileStatus = :profileStatus)
        """)
    Page<EmployerProfile> findByFilters(
            @Param("employerType") EmployerType employerType,
            @Param("profileStatus") ProfileStatus profileStatus,
            Pageable pageable
    );
}
