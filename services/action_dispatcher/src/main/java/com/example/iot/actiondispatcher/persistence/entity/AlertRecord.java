package com.example.iot.actiondispatcher.persistence.entity;

import com.example.iot.actiondispatcher.domain.model.DecisionAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerts")
public class AlertRecord {
    @Id
    private Long id;

    @Column(name = "alert_id", nullable = false)
    private String alertId;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String status;

    protected AlertRecord() { }

    public AlertRecord(String alertId, String sensorId, String severity) {
        if (alertId == null || alertId.isBlank()) throw new IllegalArgumentException("alertId es obligatorio");
        if (sensorId == null || sensorId.isBlank()) throw new IllegalArgumentException("sensorId es obligatorio");
        if (!"CRITICAL".equals(severity) && !"WARNING".equals(severity)) throw new IllegalArgumentException("severity no permitida");
        this.alertId = alertId;
        this.sensorId = sensorId;
        this.severity = severity;
        this.status = "PENDING";
    }

    public String alertId() { return alertId; }
    public String sensorId() { return sensorId; }
    public String severity() { return severity; }
    public String status() { return status; }

    public void resolveWith(DecisionAction action) {
        if (!"PENDING".equals(status)) throw new IllegalStateException("La alerta ya fue resuelta");
        if (action == null || !action.isAllowedFor(severity)) {
            throw new IllegalArgumentException("Acción no permitida para la severidad de la alerta");
        }
        status = action == DecisionAction.IGNORAR_10_MINUTOS ? "IGNORED" : "RESOLVED";
    }
}
