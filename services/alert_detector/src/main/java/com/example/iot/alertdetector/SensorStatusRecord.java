package com.example.iot.alertdetector;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SensorStatusRecord(
        @JsonProperty("sensor_id") String sensorId,
        double vibration,
        String status,
        String timestamp) {
    public SensorStatusRecord {
        new SensorId(sensorId);
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) throw new IllegalArgumentException("vibration fuera de rango");
        SensorStatus.valueOf(status);
        if (timestamp == null || timestamp.isBlank()) throw new IllegalArgumentException("timestamp es obligatorio");
    }
}
