
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.Skill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Page<Skill> findByActiveTrue(Pageable pageable);

    List<Skill> findByActiveTrue();

    Page<Skill> findByActive(boolean active, Pageable pageable);
}
