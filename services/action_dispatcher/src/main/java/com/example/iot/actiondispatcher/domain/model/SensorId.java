package com.example.iot.actiondispatcher.domain.model;

public record SensorId(String value) {
    public SensorId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("sensorId es obligatorio");
    }
}
