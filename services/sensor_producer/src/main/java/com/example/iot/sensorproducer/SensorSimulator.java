
package com.example.iot.sensorproducer;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SensorSimulator {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "sensor_data";

    private static final String[] SENSORS = {
        "sensor_A", "sensor_B", "sensor_C", "sensor_D",
        "sensor_E", "sensor_F", "sensor_G", "sensor_H",
        "sensor_I", "sensor_J"
    };

    public SensorSimulator(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void generateSensorData() {

        for (String sensorId : SENSORS) {

            double vibration = generateVibration();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("sensor_id", sensorId);
            data.put("vibration", vibration);
            data.put("timestamp", Instant.now().toString());

            kafkaTemplate.send(TOPIC, sensorId, data);

            System.out.println(
                "[KAFKA] " + sensorId +
                " -> Vibracion: " + vibration
            );
        }
    }

    private double generateVibration() {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int probability = random.nextInt(100);

        if (probability < 70) {
            return random.nextDouble(20, 70);
        }

        if (probability < 90) {
            return random.nextDouble(76, 90);
        }

        return random.nextDouble(91, 100);
    }
}
