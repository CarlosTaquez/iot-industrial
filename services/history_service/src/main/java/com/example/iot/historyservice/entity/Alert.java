package com.example.iot.historyservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String severity;

    @Column(name = "vibration_value")
    private Double vibrationValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    protected Alert() {}

    public Alert(String sensorId, String severity, Double vibrationValue, String payload) {
        this.sensorId = sensorId;
        this.severity = severity;
        this.vibrationValue = vibrationValue;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getSeverity() { return severity; }
    public Double getVibrationValue() { return vibrationValue; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
}