package com.example.iot.alertdetector;

public record SensorId(String value) {
    public SensorId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("sensor_id es obligatorio");
    }
}
