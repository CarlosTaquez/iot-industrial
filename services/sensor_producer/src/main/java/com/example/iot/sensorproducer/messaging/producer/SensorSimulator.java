
package com.example.iot.sensorproducer.messaging.producer;

import com.example.iot.sensorproducer.messaging.event.VibrationReadingRecord;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<String, OperatingCondition> conditions = new ConcurrentHashMap<>();

    public SensorSimulator(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void generateSensorData() {

        for (String sensorId : SENSORS) {
            publishReading(sensorId, generateVibration(sensorId));
        }
    }

    public VibrationReadingRecord simulateReading() {
        String sensorId = SENSORS[ThreadLocalRandom.current().nextInt(SENSORS.length)];
        return publishReading(sensorId, generateVibration(sensorId));
    }

    private VibrationReadingRecord publishReading(String sensorId, double vibration) {
        VibrationReadingRecord reading = new VibrationReadingRecord(
                sensorId, vibration, java.time.Instant.now().toString());
        kafkaTemplate.send(TOPIC, sensorId, reading);
        System.out.println("[KAFKA] " + sensorId + " -> Vibracion: " + vibration);
        return reading;
    }

    private double generateVibration(String sensorId) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        OperatingCondition condition = conditions.compute(sensorId,
                (id, current) -> nextCondition(current, random));

        return switch (condition) {
            case NORMAL -> random.nextDouble(20, 70);
            case WARNING -> random.nextDouble(78, 90);
            case CRITICAL -> random.nextDouble(91, 100);
        };
    }

    private OperatingCondition nextCondition(OperatingCondition current, ThreadLocalRandom random) {
        if (current == null) return OperatingCondition.NORMAL;

        double transition = random.nextDouble();
        return switch (current) {
            case NORMAL -> transition < 0.005
                    ? OperatingCondition.WARNING : OperatingCondition.NORMAL;
            case WARNING -> transition < 0.04
                    ? OperatingCondition.CRITICAL
                    : transition < 0.14 ? OperatingCondition.NORMAL : OperatingCondition.WARNING;
            case CRITICAL -> transition < 0.08
                    ? OperatingCondition.WARNING
                    : transition < 0.12 ? OperatingCondition.NORMAL : OperatingCondition.CRITICAL;
        };
    }

    private enum OperatingCondition { NORMAL, WARNING, CRITICAL }
}
