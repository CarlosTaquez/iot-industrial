package com.example.iot.alertrouter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record HumanAlertRecord(
        @JsonProperty("alert_id") String alertId,
        @JsonProperty("sensor_id") String sensorId,
        String type,
        double vibration,
        String timestamp,
        List<String> actions) {
    public HumanAlertRecord {
        new AlertId(alertId);
        new SensorId(sensorId);
        AlertSeverity.from(type);
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) throw new IllegalArgumentException("Vibración fuera de rango");
        if (timestamp == null || timestamp.isBlank()) throw new IllegalArgumentException("timestamp es obligatorio");
        actions = List.copyOf(actions);
        if (actions.isEmpty()) throw new IllegalArgumentException("Debe ofrecer al menos una acción");
    }

    public static HumanAlertRecord from(IndustrialAlert alert) {
        return new HumanAlertRecord(alert.alertId(), alert.sensorId(), alert.severity().name(),
                alert.vibration(), alert.timestamp(), alert.availableActions());
    }
}
