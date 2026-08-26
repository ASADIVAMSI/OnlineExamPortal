package com.kspiders.app.onlineexamportal.dao;

// USING JPA: this repository stores each selected answer in a submission.

import com.kspiders.app.onlineexamportal.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
