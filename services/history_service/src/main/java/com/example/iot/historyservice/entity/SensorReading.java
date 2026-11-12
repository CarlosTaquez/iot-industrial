package com.example.iot.historyservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private Double vibration;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "stored_at", insertable = false, updatable = false)
    private Instant storedAt;

    protected SensorReading() {}

    public SensorReading(String sensorId, Double vibration, Instant recordedAt) {
        this.sensorId = sensorId;
        this.vibration = vibration;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public Double getVibration() { return vibration; }
    public Instant getRecordedAt() { return recordedAt; }
    public Instant getStoredAt() { return storedAt; }
}