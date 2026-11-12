package com.example.iot.maintenanceworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class MaintenanceWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaintenanceWorkerApplication.class, args);
    }
}
