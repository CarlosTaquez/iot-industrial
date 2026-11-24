package com.example.iot.alertdetector.messaging.event;

import com.example.iot.alertdetector.domain.model.AlertId;
import com.example.iot.alertdetector.domain.model.SensorId;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AlertEventRecord(
        @JsonProperty("alert_id") String alertId,
        @JsonProperty("sensor_id") String sensorId,
        String type,
        double vibration,
        String timestamp) {
    public AlertEventRecord {
        new AlertId(alertId);
        new SensorId(sensorId);
        if (!"CRITICAL".equals(type) && !"WARNING".equals(type)) throw new IllegalArgumentException("type no permitido");
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) throw new IllegalArgumentException("vibration fuera de rango");
        if (timestamp == null || timestamp.isBlank()) throw new IllegalArgumentException("timestamp es obligatorio");
    }
}
