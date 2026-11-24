package com.example.iot.actiondispatcher.domain.model;

public enum DecisionAction {
    APAGADO_INMEDIATO("actuator"),
    IGNORAR_10_MINUTOS("actuator"),
    PROGRAMAR_MANTENIMIENTO_AHORA("maintenance"),
    RECONOCER_Y_ESPERAR_24H("maintenance");

    private final String routingKey;

    DecisionAction(String routingKey) { this.routingKey = routingKey; }

    public String routingKey() { return routingKey; }

    public boolean isAllowedFor(String severity) {
        if (severity == null) return false;
        return switch (this) {
            case APAGADO_INMEDIATO, IGNORAR_10_MINUTOS -> "CRITICAL".equalsIgnoreCase(severity);
            case PROGRAMAR_MANTENIMIENTO_AHORA, RECONOCER_Y_ESPERAR_24H -> "WARNING".equalsIgnoreCase(severity);
        };
    }

    public static DecisionAction from(String value) {
        try {
            return valueOf(value == null ? "" : value.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Acción no permitida: " + value);
        }
    }
}
