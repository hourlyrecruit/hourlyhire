
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.Skill;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findByActiveTrue();
}
