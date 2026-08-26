package com.kspiders.app.onlineexamportal.dao;

// USING JPA: this repository reads and saves assessment questions.

import com.kspiders.app.onlineexamportal.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuestionSetIdOrderById(Long questionSetId);

    long countByQuestionSetId(Long questionSetId);
}
