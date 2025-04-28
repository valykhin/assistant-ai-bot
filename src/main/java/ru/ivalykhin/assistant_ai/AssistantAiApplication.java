package ru.ivalykhin.assistant_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AssistantAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantAiApplication.class, args);
    }

}
