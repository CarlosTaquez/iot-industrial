package com.example.iot.alertrouter;

public enum AlertSeverity {
    CRITICAL, WARNING;

    public static AlertSeverity from(String value) {
        try {
            return valueOf(value == null ? "" : value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Severidad de alerta no permitida: " + value);
        }
    }
}
