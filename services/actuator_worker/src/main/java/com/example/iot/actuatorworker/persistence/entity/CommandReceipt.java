package com.example.iot.actuatorworker.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "command_receipts")
public class CommandReceipt {
    @Id
    private String id;
    @Column(name = "decision_id", nullable = false)
    private String decisionId;
    @Column(name = "worker_name", nullable = false)
    private String workerName;
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
    protected CommandReceipt() { }
    public CommandReceipt(String decisionId) {
        this.decisionId = decisionId;
        this.workerName = "actuator";
        this.id = workerName + ":" + decisionId;
        this.processedAt = Instant.now();
    }
}
