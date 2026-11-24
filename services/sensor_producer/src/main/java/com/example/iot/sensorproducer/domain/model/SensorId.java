package com.example.iot.sensorproducer.domain.model;

public record SensorId(String value) {
    public SensorId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("sensor_id es obligatorio");
    }
}
