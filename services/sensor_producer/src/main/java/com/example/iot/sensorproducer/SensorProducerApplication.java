
package com.example.iot.sensorproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SensorProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensorProducerApplication.class, args);
    }
}
