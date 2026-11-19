package com.example.iot.actiondispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActionDispatcherApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                ActionDispatcherApplication.class,
                args
        );

    }
}
