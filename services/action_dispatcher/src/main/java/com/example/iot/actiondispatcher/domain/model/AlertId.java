package com.example.iot.actiondispatcher.domain.model;

public record AlertId(String value) {
    public AlertId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("alertId es obligatorio");
    }
}
