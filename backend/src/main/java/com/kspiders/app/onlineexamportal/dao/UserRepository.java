package com.kspiders.app.onlineexamportal.dao;

// USING JPA: this repository provides database operations for registered users.

import com.kspiders.app.onlineexamportal.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
