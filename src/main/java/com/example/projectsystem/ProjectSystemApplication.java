package com.example.projectsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProjectSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectSystemApplication.class, args);
    }

}
