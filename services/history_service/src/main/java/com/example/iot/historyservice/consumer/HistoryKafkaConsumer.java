package com.example.iot.historyservice.consumer;

import com.example.iot.historyservice.entity.Alert;
import com.example.iot.historyservice.entity.SensorReading;
import com.example.iot.historyservice.repository.AlertRepository;
import com.example.iot.historyservice.repository.SensorReadingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HistoryKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(HistoryKafkaConsumer.class);

    private final ObjectMapper mapper;
    private final SensorReadingRepository readings;
    private final AlertRepository alerts;
    private final long sampleSeconds;
    private final Map<String, Instant> lastStored = new ConcurrentHashMap<>();

    public HistoryKafkaConsumer(ObjectMapper mapper,
                                SensorReadingRepository readings,
                                AlertRepository alerts,
                                @Value("${history.readings.sample-seconds:10}") long sampleSeconds) {
        this.mapper = mapper;
        this.readings = readings;
        this.alerts = alerts;
        this.sampleSeconds = sampleSeconds;
    }

    @KafkaListener(topics = "sensor_data", groupId = "history-service")
    public void onReading(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            String sensorId = JsonFields.text(node, "sensor_id", "sensorId");
            Double vibration = JsonFields.number(node, "vibration");
            if (sensorId == null || vibration == null) {
                log.warn("Lectura ignorada, faltan campos: {}", json);
                return;
            }

            Instant recordedAt = parseInstant(JsonFields.text(node, "timestamp"));

            Instant last = lastStored.get(sensorId);
            if (last != null && recordedAt.isBefore(last.plusSeconds(sampleSeconds))) {
                return;
            }
            lastStored.put(sensorId, recordedAt);

            readings.save(new SensorReading(sensorId, vibration, recordedAt));
        } catch (Exception e) {
            log.error("Error guardando lectura: {}", json, e);
        }
    }

    @KafkaListener(topics = "alerts_critical", groupId = "history-service")
    public void onCritical(String json) {
        saveAlert(json, "CRITICAL");
    }

    @KafkaListener(topics = "alerts_warning", groupId = "history-service")
    public void onWarning(String json) {
        saveAlert(json, "WARNING");
    }

    private void saveAlert(String json, String severity) {
        try {
            JsonNode node = mapper.readTree(json);
            String sensorId = JsonFields.text(node, "sensor_id", "sensorId", "sensor");
            Double value = JsonFields.number(node, "vibration", "vibration_value", "vibrationValue", "value");
            String alertId = JsonFields.text(node, "alert_id", "alertId");
            if (alertId != null && alerts.existsByAlertId(alertId)) {
                return;
            }
            Instant occurredAt = parseInstant(JsonFields.text(node, "timestamp"));
            alerts.save(new Alert(sensorId != null ? sensorId : "UNKNOWN", severity, value, json,
                    alertId, occurredAt));
        } catch (Exception e) {
            log.error("Error guardando alerta {}: {}", severity, json, e);
        }
    }

    private Instant parseInstant(String text) {
        try {
            return text != null ? Instant.parse(text) : Instant.now();
        } catch (Exception e) {
            return Instant.now();
        }
    }
}
