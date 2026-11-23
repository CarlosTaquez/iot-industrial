package com.example.iot.actiondispatcher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "operator_decisions")
public class DecisionAuditRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "decision_id", nullable = false, unique = true)
    private String decisionId;
    @Column(name = "alert_id")
    private String alertId;
    @Column(name = "sensor_id", nullable = false)
    private String sensorId;
    @Column(nullable = false)
    private String action;
    @Column(name = "routing_key")
    private String routingKey;
    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;
    @Column(nullable = false)
    private String status;

    protected DecisionAuditRecord() { }

    public DecisionAuditRecord(MaintenanceDecision decision, String payload) {
        this.decisionId = decision.decisionId();
        this.alertId = decision.alertId();
        this.sensorId = decision.sensorId();
        this.action = decision.action().name();
        this.routingKey = decision.action().routingKey();
        this.payload = payload;
        this.status = "RECEIVED";
    }

    public void markDispatched() { this.status = "DISPATCHED"; }
}
