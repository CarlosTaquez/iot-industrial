package com.example.iot.actiondispatcher;

public record AlertId(String value) {
    public AlertId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("alertId es obligatorio");
    }
}
