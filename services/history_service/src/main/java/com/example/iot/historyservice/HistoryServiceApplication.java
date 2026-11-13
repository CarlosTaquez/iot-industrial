package com.example.iot.historyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HistoryServiceApplication {

    public static void main(String[] args) {
        // Hibernate usa Byte Buddy, que puede rechazar JDK muy nuevos (como el 25).
        System.setProperty("net.bytebuddy.experimental", "true");
        SpringApplication.run(HistoryServiceApplication.class, args);
    }
}