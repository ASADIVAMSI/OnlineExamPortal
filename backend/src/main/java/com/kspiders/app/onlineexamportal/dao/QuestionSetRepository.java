package com.kspiders.app.onlineexamportal.dao;

import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing QuestionSet entities.
 */
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

    Optional<QuestionSet> findByName(String name);
}
