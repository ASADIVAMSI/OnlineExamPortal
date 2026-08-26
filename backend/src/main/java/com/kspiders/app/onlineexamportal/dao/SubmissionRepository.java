package com.kspiders.app.onlineexamportal.dao;

// USING JPA: this repository stores completed assessment submissions.

import com.kspiders.app.onlineexamportal.entity.Submission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    boolean existsByUserId(Long userId);

    Optional<Submission> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "questionSet", "answers", "answers.question"})
    List<Submission> findAllByOrderBySubmittedAtDesc();
}
