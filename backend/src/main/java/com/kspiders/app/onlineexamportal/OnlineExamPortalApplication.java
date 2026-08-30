package com.kspiders.app.onlineexamportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Online Exam Portal Spring Boot Application backend.
 * Bootstraps embedded Tomcat server, Spring Data repositories, JPA entities, and REST controllers.
 */
@SpringBootApplication
public class OnlineExamPortalApplication {

    /**
     * Main method launching the Spring Boot framework.
     *
     * @param args Command-line arguments passed to application initialization.
     */
    public static void main(String[] args) {
        SpringApplication.run(OnlineExamPortalApplication.class, args);
    }
}
