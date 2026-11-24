package com.example.iot.alertdetector.domain.model;

public record AlertId(String value) {
    public AlertId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("alert_id es obligatorio");
    }
}
