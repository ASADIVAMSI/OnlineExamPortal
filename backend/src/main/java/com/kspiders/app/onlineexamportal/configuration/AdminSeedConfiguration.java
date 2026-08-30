package com.kspiders.app.onlineexamportal.configuration;

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Configuration class that initializes default administrator credentials upon application startup.
 * Ensures an admin account (admin@kspiders.com / admin@123) exists in the database.
 */
@Configuration
public class AdminSeedConfiguration {

    /**
     * Seeds or updates the default administrator user with credentials:
     * Username/Email: admin@kspiders.com
     * Password: admin@123
     *
     * @param userRepository  Repository interface for managing User entity persistence.
     * @param passwordEncoder Password encoder utility to securely hash the default admin password.
     * @return A CommandLineRunner task executed after Spring container initialization.
     */
    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@kspiders.com";
            String adminRawPassword = "admin@123";

            // Check if admin@kspiders.com already exists
            Optional<User> existingKspidersAdmin = userRepository.findByEmailIgnoreCase(adminEmail);
            if (existingKspidersAdmin.isPresent()) {
                User admin = existingKspidersAdmin.get();
                admin.setPassword(passwordEncoder.encode(adminRawPassword));
                admin.setRole(User.Role.ADMIN);
                admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
                userRepository.save(admin);
            } else {
                // If legacy admin@example.com exists, update its email and password
                Optional<User> legacyAdmin = userRepository.findByEmailIgnoreCase("admin@example.com");
                if (legacyAdmin.isPresent()) {
                    User admin = legacyAdmin.get();
                    admin.setEmail(adminEmail);
                    admin.setPassword(passwordEncoder.encode(adminRawPassword));
                    admin.setRole(User.Role.ADMIN);
                    admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
                    userRepository.save(admin);
                } else {
                    // Create new admin user
                    User admin = new User("Portal Admin", adminEmail, passwordEncoder.encode(adminRawPassword));
                    admin.setRole(User.Role.ADMIN);
                    admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
                    userRepository.save(admin);
                }
            }
        };
    }
}
