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

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(nullable = false)
    private String action;

    @Column(name = "routing_key")
    private String routingKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "decided_at", insertable = false, updatable = false)
    private Instant decidedAt;

    protected OperatorDecision() {}

    public OperatorDecision(String sensorId, String action, String routingKey, String payload) {
        this.sensorId = sensorId;
        this.action = action;
        this.routingKey = routingKey;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getAction() { return action; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public Instant getDecidedAt() { return decidedAt; }
}