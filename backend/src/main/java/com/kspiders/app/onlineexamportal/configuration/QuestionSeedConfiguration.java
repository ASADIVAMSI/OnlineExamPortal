package com.kspiders.app.onlineexamportal.configuration;

// Adds the initial programming questions used by the assessments.

import com.kspiders.app.onlineexamportal.dao.QuestionRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.entity.Question;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionSeedConfiguration {

    @Bean
    CommandLineRunner seedQuestions(QuestionSetRepository setRepository, QuestionRepository questionRepository) {
        return args -> setRepository.findAll().forEach(set -> {
            if (questionRepository.countByQuestionSetId(set.getId()) == 0) {
                for (int number = 1; number <= 30; number++) {
                    Question question = new Question(set,
                        set.getName() + ": Which programming concept is represented by example " + number + "?",
                        "Encapsulation", "Compilation", "Inheritance", "Recursion", "MULTIPLE_CHOICE");
                    question.setCorrectOption(correctOption(number));
                    questionRepository.save(question);
                }
            } else {
                questionRepository.findByQuestionSetIdOrderById(set.getId()).forEach(question -> {
                    if (question.getCorrectOption() == null) {
                        question.setCorrectOption(correctOption((int) (question.getId() % 30) + 1));
                        questionRepository.save(question);
                    }
                });
            }
        });
    }

    private String correctOption(int number) {
        return switch (number % 4) {
            case 1 -> "A";
            case 2 -> "B";
            case 3 -> "C";
            default -> "D";
        };
    }
}
