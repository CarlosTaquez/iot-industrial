package com.example.iot.actiondispatcher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "decision_outbox")
public class DecisionOutboxRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "decision_id", nullable = false, length = 36)
    private String decisionId;
    @Column(name = "exchange_name", nullable = false)
    private String exchangeName;
    @Column(name = "routing_key", nullable = false)
    private String routingKey;
    @Column(nullable = false, columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String payload;
    @Column(name = "delay_ms", nullable = false)
    private long delayMs;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    @Column(nullable = false)
    private int attempts;
    @Column(name = "last_error", length = 1000)
    private String lastError;

    protected DecisionOutboxRecord() { }

    public DecisionOutboxRecord(MaintenanceDecision decision, String payload) {
        this.decisionId = decision.decisionId();
        boolean delayed = decision.action() == DecisionAction.RECONOCER_Y_ESPERAR_24H;
        this.exchangeName = delayed ? RabbitMQConfig.DELAYED_EXCHANGE : RabbitMQConfig.EXCHANGE_NAME;
        this.routingKey = decision.action().routingKey();
        this.payload = payload;
        this.delayMs = delayed ? 24L * 60 * 60 * 1000 : 0;
        this.createdAt = Instant.now();
    }

    public void markPublished() {
        publishedAt = Instant.now();
        lastError = null;
    }

    public void markFailed(Exception exception) {
        attempts++;
        String message = exception.getMessage();
        lastError = message == null ? exception.getClass().getSimpleName()
                : message.substring(0, Math.min(message.length(), 1000));
    }

    public Long id() { return id; }
    public String decisionId() { return decisionId; }
    public String exchangeName() { return exchangeName; }
    public String routingKey() { return routingKey; }
    public String payload() { return payload; }
    public long delayMs() { return delayMs; }
}
