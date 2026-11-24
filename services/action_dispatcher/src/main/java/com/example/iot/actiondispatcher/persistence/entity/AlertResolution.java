package com.example.iot.actiondispatcher.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "alert_resolutions")
public class AlertResolution {
    @Id
    @Column(name = "alert_id", length = 64)
    private String alertId;

    @Column(name = "decision_id", nullable = false, unique = true, length = 36)
    private String decisionId;

    @Column(name = "resolved_at", nullable = false)
    private Instant resolvedAt;

    protected AlertResolution() { }

    public AlertResolution(String alertId, String decisionId) {
        if (alertId == null || alertId.isBlank()) throw new IllegalArgumentException("alertId es obligatorio");
        if (decisionId == null || decisionId.isBlank()) throw new IllegalArgumentException("decisionId es obligatorio");
        this.alertId = alertId;
        this.decisionId = decisionId;
        this.resolvedAt = Instant.now();
    }
}
