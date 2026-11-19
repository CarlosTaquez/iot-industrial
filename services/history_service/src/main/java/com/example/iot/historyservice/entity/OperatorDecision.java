package com.example.iot.historyservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "operator_decisions")
public class OperatorDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "decision_id", nullable = false, unique = true, length = 36)
    private String decisionId;

    @Column(name = "alert_id")
    private String alertId;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(nullable = false)
    private String action;

    @Column(name = "routing_key")
    private String routingKey;

    @Column(nullable = false)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "decided_at", insertable = false, updatable = false)
    private Instant decidedAt;

    protected OperatorDecision() {}

    public OperatorDecision(String decisionId, String sensorId, String action, String routingKey, String payload,
                            String alertId) {
        if (decisionId == null || decisionId.isBlank()) throw new IllegalArgumentException("decision_id es obligatorio");
        if (sensorId == null || sensorId.isBlank()) throw new IllegalArgumentException("sensor_id es obligatorio");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action es obligatorio");
        if (payload == null || payload.isBlank()) throw new IllegalArgumentException("payload es obligatorio");
        this.decisionId = decisionId;
        this.sensorId = sensorId;
        this.action = action;
        this.routingKey = routingKey;
        this.payload = payload;
        this.alertId = alertId;
        this.status = "DISPATCHED";
    }

    public Long getId() { return id; }
    public String getDecisionId() { return decisionId; }
    public String getAlertId() { return alertId; }
    public String getSensorId() { return sensorId; }
    public String getAction() { return action; }
    public String getRoutingKey() { return routingKey; }
    public String getStatus() { return status; }
    public String getPayload() { return payload; }
    public Instant getDecidedAt() { return decidedAt; }
}
