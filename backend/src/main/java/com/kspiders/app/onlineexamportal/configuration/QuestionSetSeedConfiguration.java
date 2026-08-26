package com.kspiders.app.onlineexamportal.configuration;

// Creates the initial question sets when the database is first prepared.

import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionSetSeedConfiguration {

    @Bean
    CommandLineRunner seedQuestionSets(QuestionSetRepository repository) {
        return args -> {
            createIfMissing(repository, "Question Set 1", "Programming basics and core syntax");
            createIfMissing(repository, "Question Set 2", "Object-oriented programming concepts");
            createIfMissing(repository, "Question Set 3", "Data structures and algorithms");
            createIfMissing(repository, "Question Set 4", "Advanced programming concepts");
        };
    }

    private void createIfMissing(QuestionSetRepository repository, String name, String description) {
        if (repository.findByName(name).isEmpty()) {
            repository.save(new QuestionSet(name, description));
        }
    }
}
