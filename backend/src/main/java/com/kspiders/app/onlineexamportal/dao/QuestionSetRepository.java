package com.kspiders.app.onlineexamportal.dao;

// Spring Data creates the database queries for QuestionSet automatically from this interface.

import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

    Optional<QuestionSet> findByName(String name);
}
