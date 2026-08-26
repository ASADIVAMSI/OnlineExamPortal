package com.kspiders.app.onlineexamportal.dao;

// USING JPA: Spring Data provides CRUD queries for user/question-set assignments.

import com.kspiders.app.onlineexamportal.entity.Assignment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByUserId(Long userId);
}
