package com.kspiders.app.onlineexamportal.dao;

import com.kspiders.app.onlineexamportal.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Question entities.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuestionSetIdOrderById(Long questionSetId);

    long countByQuestionSetId(Long questionSetId);
}
