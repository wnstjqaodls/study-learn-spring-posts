package com.example.studylearnspringposts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyLearnSpringPostsApplication {

    public static void main (String[] args) {
        SpringApplication.run(StudyLearnSpringPostsApplication.class, args);
    }

}
