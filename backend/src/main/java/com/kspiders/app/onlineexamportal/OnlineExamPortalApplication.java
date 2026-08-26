package com.kspiders.app.onlineexamportal;

// Application entry point; Spring Boot discovers the controllers, services, and repositories below this package.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineExamPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExamPortalApplication.class, args);
    }
}
