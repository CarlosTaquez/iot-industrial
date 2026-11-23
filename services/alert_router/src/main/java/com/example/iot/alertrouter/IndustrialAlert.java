package com.example.iot.alertrouter;

import java.util.List;

/** Domain entity for an alert awaiting an operator response. */
public final class IndustrialAlert {

    private final AlertId alertId;
    private final SensorId sensorId;
    private final AlertSeverity severity;
    private final double vibration;
    private final String timestamp;
    private AlertLifecycle lifecycle = AlertLifecycle.PENDING;

    public IndustrialAlert(String alertId, String sensorId, AlertSeverity severity, double vibration, String timestamp) {
        if (severity == null) throw new IllegalArgumentException("La severidad es obligatoria");
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) throw new IllegalArgumentException("Vibración fuera de rango");
        if (timestamp == null || timestamp.isBlank()) throw new IllegalArgumentException("timestamp es obligatorio");
        this.alertId = new AlertId(alertId);
        this.sensorId = new SensorId(sensorId);
        this.severity = severity;
        this.vibration = vibration;
        this.timestamp = timestamp;
    }

    public List<String> availableActions() {
        return severity == AlertSeverity.CRITICAL
                ? List.of("APAGADO_INMEDIATO", "IGNORAR_10_MINUTOS")
                : List.of("PROGRAMAR_MANTENIMIENTO_AHORA", "RECONOCER_Y_ESPERAR_24H");
    }

    public void resolveWith(String action) {
        if (lifecycle != AlertLifecycle.PENDING) throw new IllegalStateException("La alerta ya fue resuelta");
        if (!availableActions().contains(action)) throw new IllegalArgumentException("Acción no permitida para " + severity + ": " + action);
        lifecycle = "IGNORAR_10_MINUTOS".equals(action) ? AlertLifecycle.IGNORED : AlertLifecycle.RESOLVED;
    }

    public String alertId() { return alertId.value(); }
    public String sensorId() { return sensorId.value(); }
    public AlertSeverity severity() { return severity; }
    public double vibration() { return vibration; }
    public String timestamp() { return timestamp; }
    public AlertLifecycle lifecycle() { return lifecycle; }

    public enum AlertLifecycle { PENDING, RESOLVED, IGNORED }
}
