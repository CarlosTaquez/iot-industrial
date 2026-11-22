package com.example.iot.historyservice.dto.response;

import com.example.iot.historyservice.entity.Alert;
import java.time.Instant;

/** Public API representation of a stored alert. */
public record AlertResponse(
        Long id,
        String alertId,
        String sensorId,
        String severity,
        Double vibrationValue,
        Instant occurredAt,
        String status,
        String payload,
        Instant createdAt) {

    public static AlertResponse from(Alert alert) {
        return new AlertResponse(alert.getId(), alert.getAlertId(), alert.getSensorId(),
                alert.getSeverity(), alert.getVibrationValue(), alert.getOccurredAt(),
                alert.getStatus(), alert.getPayload(), alert.getCreatedAt());
    }
}
