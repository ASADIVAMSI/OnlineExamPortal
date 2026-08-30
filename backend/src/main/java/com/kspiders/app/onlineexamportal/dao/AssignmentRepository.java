package com.kspiders.app.onlineexamportal.dao;

import com.kspiders.app.onlineexamportal.entity.Assignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository interface for managing Assignment entity instances.
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findTopByUserIdOrderByIdDesc(Long userId);

    List<Assignment> findByUserIdOrderByIdDesc(Long userId);

    default Optional<Assignment> findByUserId(Long userId) {
        return findTopByUserIdOrderByIdDesc(userId);
    }
}
