package com.kspiders.app.onlineexamportal.dao;

import com.kspiders.app.onlineexamportal.entity.Submission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

/**
 * Repository interface for managing Submission entities.
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    boolean existsByUserId(Long userId);

    Optional<Submission> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "questionSet", "answers", "answers.question"})
    List<Submission> findAllByOrderBySubmittedAtDesc();
}
