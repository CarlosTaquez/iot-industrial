
package com.example.iot.alertdetector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AlertDetector {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final Map<String, Deque<Double>> sensorWindows =
            new HashMap<>();

    public AlertDetector(
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "sensor_data",
            groupId = "alert-detector-group"
    )
    public void processSensorData(String message) {

        try {
            JsonNode data = objectMapper.readTree(message);

            String sensorId = data.get("sensor_id").asText();
            double vibration = data.get("vibration").asDouble();

            Deque<Double> window = sensorWindows.computeIfAbsent(
                    sensorId,
                    key -> new ArrayDeque<>()
            );

            window.addLast(vibration);

            if (window.size() > 3) {
                window.removeFirst();
            }

            System.out.println(
                    "[DETECTOR] " + sensorId +
                    " -> Vibracion: " + vibration
            );

            // Publicar cada lectura para el monitoreo en tiempo real
            String status = "NORMAL";

            if (vibration > 90) {
                status = "CRITICAL";
            } else if (
                    window.size() == 3 &&
                    window.stream().allMatch(value -> value > 75)
            ) {
                status = "WARNING";
            }

            Map<String, Object> sensorStatus = new LinkedHashMap<>();

            sensorStatus.put("sensor_id", sensorId);
            sensorStatus.put("vibration", vibration);
            sensorStatus.put("status", status);
            sensorStatus.put("timestamp", Instant.now().toString());

            kafkaTemplate.send("sensor_status", sensorId, sensorStatus);

            if (vibration > 90) {

                publishAlert(
                        sensorId,
                        vibration,
                        "CRITICAL",
                        "alerts_critical"
                );

            } else if (
                    window.size() == 3 &&
                    window.stream().allMatch(value -> value > 75)
            ) {

                publishAlert(
                        sensorId,
                        vibration,
                        "WARNING",
                        "alerts_warning"
                );

                window.clear();
            }

        } catch (Exception e) {
            System.err.println(
                    "Error procesando mensaje: " + e.getMessage()
            );
        }
    }

    private void publishAlert(
            String sensorId,
            double vibration,
            String type,
            String topic) {

        Map<String, Object> alert = new LinkedHashMap<>();

        alert.put("alert_id", java.util.UUID.randomUUID().toString());
        alert.put("sensor_id", sensorId);
        alert.put("type", type);
        alert.put("vibration", vibration);
        alert.put("timestamp", Instant.now().toString());

        kafkaTemplate.send(topic, sensorId, alert);

        System.out.println(
                "[ALERTA " + type + "] " +
                sensorId + " -> " + vibration
        );
    }
}
