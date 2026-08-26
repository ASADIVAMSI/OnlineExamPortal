package com.kspiders.app.onlineexamportal.configuration;

// Seeds the default administrator account when the application starts.

import com.kspiders.app.onlineexamportal.dao.UserRepository;
import com.kspiders.app.onlineexamportal.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeedConfiguration {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmailIgnoreCase("admin@example.com").isEmpty()) {
                User admin = new User("Portal Admin", "admin@example.com",
                    passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                admin.setApprovalStatus(User.ApprovalStatus.APPROVED);
                userRepository.save(admin);
            }
        };
    }
}
