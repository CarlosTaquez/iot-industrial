package com.example.iot.alertdetector;

public record AlertId(String value) {
    public AlertId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("alert_id es obligatorio");
    }
}
