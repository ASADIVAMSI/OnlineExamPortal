package com.kspiders.app.onlineexamportal.dao;

import com.kspiders.app.onlineexamportal.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing UserAnswer entities.
 */
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
