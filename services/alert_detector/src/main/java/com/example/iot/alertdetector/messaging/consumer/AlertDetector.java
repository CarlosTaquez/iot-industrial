package com.example.iot.alertdetector.messaging.consumer;

import com.example.iot.alertdetector.domain.model.SensorStatus;
import com.example.iot.alertdetector.domain.model.SensorVibrationWindow;
import com.example.iot.alertdetector.domain.strategy.CriticityStrategy;
import com.example.iot.alertdetector.messaging.event.AlertEventRecord;
import com.example.iot.alertdetector.messaging.event.SensorStatusRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertDetector {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, SensorVibrationWindow> windows = new HashMap<>();
    private final CriticityStrategy criticityStrategy;

    public AlertDetector(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                         CriticityStrategy criticityStrategy) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.criticityStrategy = criticityStrategy;
    }

    @KafkaListener(topics = "sensor_data", groupId = "alert-detector-group")
    public void processSensorData(String message) {
        try {
            JsonNode data = objectMapper.readTree(message);
            String sensorId = data.path("sensor_id").asText();
            double vibration = data.path("vibration").asDouble(Double.NaN);
            if (sensorId.isBlank() || !Double.isFinite(vibration)) {
                throw new IllegalArgumentException("sensor_id y vibration son obligatorios");
            }

            SensorVibrationWindow window = windows.computeIfAbsent(sensorId, SensorVibrationWindow::new);
            SensorStatus status = window.registerReading(vibration, criticityStrategy);
            kafkaTemplate.send("sensor_status", sensorId,
                    new SensorStatusRecord(sensorId, vibration, status.name(), Instant.now().toString()));

            if (window.criticalThresholdReached()) {
                publishAlert(sensorId, vibration, status, "alerts_critical");
                window.acknowledgeCritical();
            } else if (window.warningThresholdReached()) {
                publishAlert(sensorId, vibration, status, "alerts_warning");
                window.acknowledgeWarning();
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private void publishAlert(String sensorId, double vibration, SensorStatus status, String topic) {
        AlertEventRecord alert = new AlertEventRecord(UUID.randomUUID().toString(), sensorId,
                status.name(), vibration, Instant.now().toString());
        kafkaTemplate.send(topic, sensorId, alert);
        System.out.println("[ALERTA " + status + "] " + sensorId + " -> " + vibration);
    }
}
