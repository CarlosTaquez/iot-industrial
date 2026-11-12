package com.example.iot.actuatorworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActuatorWorkerApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                ActuatorWorkerApplication.class,
                args
        );

    }
}
