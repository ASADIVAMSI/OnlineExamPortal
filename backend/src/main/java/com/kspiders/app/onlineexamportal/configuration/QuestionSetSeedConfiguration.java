package com.kspiders.app.onlineexamportal.configuration;

import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Configuration class that populates or updates the initial seed Question Sets upon application startup.
 * Sets up predefined Java exam modules (e.g. Core Java Basics, OOPs, Encapsulation, Exception Handling).
 */
@Configuration
public class QuestionSetSeedConfiguration {

    /**
     * Seeds initial question set categories if they do not already exist, or updates their titles and descriptions.
     *
     * @param repository QuestionSetRepository instance used to query and persist QuestionSet entity data.
     * @return CommandLineRunner callback executed on Spring Boot startup.
     */
    @Bean
    CommandLineRunner seedQuestionSets(QuestionSetRepository repository) {
        return args -> {
            // Seed Module 1: Core Java Basics
            createOrUpdate(repository, "Question Set 1 – 30 Questions", "Core Java Basics: Syntax, Data Types & Control Flow");
            // Seed Module 2: Core Java OOPs
            createOrUpdate(repository, "Question Set 2 – 30 Questions", "Core Java OOPs: Classes, Objects & Strings");
            // Seed Module 3: Core Java Pillars
            createOrUpdate(repository, "Question Set 3 – 30 Questions", "Core Java Pillars: Encapsulation, Inheritance & Static");
            // Seed Module 4: Core Java Features
            createOrUpdate(repository, "Question Set 4 – 30 Questions", "Core Java Features: Interfaces & Exception Handling");
        };
    }

    /**
     * Helper method to insert a new QuestionSet entity or update an existing entity matching current or legacy naming formats.
     *
     * @param repository  The repository instance for database interaction.
     * @param name        The target name for the question set.
     * @param description The updated descriptive summary for the question set.
     */
    private void createOrUpdate(QuestionSetRepository repository, String name, String description) {
        // Extract legacy short name prefix (e.g. "Question Set 1") for backward compatibility checks
        String legacyName = name.split(" – ")[0];
        
        // Search by new full name or legacy short name
        Optional<QuestionSet> existing = repository.findByName(name)
            .or(() -> repository.findByName(legacyName));

        if (existing.isPresent()) {
            // Update title and description for an existing record
            QuestionSet set = existing.get();
            set.setName(name);
            set.setDescription(description);
            repository.save(set);
        } else {
            // Save new question set entity if not present
            repository.save(new QuestionSet(name, description));
        }
    }
}
