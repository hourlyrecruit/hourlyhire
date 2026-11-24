
package com.hourlyhire.discovery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hourlyhire.discovery.entity.WorkerProfile;

@Repository
public interface WorkerDiscoveryRepository
        extends JpaRepository<WorkerProfile, Long> {

    @Query(
        value = """
            SELECT wp.*
            FROM public.worker_profiles wp
            WHERE wp.is_discoverable = true
              AND wp.profile_status = 'ACTIVE'

              AND (
                  :skillId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM public.worker_skills ws
                      JOIN public.skills s
                        ON s.id = ws.skill_id
                      WHERE ws.worker_profile_id = wp.id
                        AND ws.skill_id = :skillId
                        AND s.active = true
                  )
              )

              AND (
                  :serviceAreaId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM public.service_areas sa
                      WHERE sa.worker_profile_id = wp.id
                        AND sa.id = :serviceAreaId
                        AND sa.active = true
                  )
              )

            ORDER BY wp.created_at DESC
            """,

        countQuery = """
            SELECT COUNT(*)
            FROM public.worker_profiles wp
            WHERE wp.is_discoverable = true
              AND wp.profile_status = 'ACTIVE'

              AND (
                  :skillId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM public.worker_skills ws
                      JOIN public.skills s
                        ON s.id = ws.skill_id
                      WHERE ws.worker_profile_id = wp.id
                        AND ws.skill_id = :skillId
                        AND s.active = true
                  )
              )

              AND (
                  :serviceAreaId IS NULL
                  OR EXISTS (
                      SELECT 1
                      FROM public.service_areas sa
                      WHERE sa.worker_profile_id = wp.id
                        AND sa.id = :serviceAreaId
                        AND sa.active = true
                  )
              )
            """,

        nativeQuery = true
    )
    Page<WorkerProfile> searchWorkers(
            @Param("skillId") Long skillId,
            @Param("serviceAreaId") Long serviceAreaId,
            Pageable pageable
    );
}
