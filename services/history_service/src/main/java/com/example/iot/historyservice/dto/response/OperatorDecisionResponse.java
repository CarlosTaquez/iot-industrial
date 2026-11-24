package com.example.iot.historyservice.dto.response;

import com.example.iot.historyservice.persistence.entity.OperatorDecision;
import java.time.Instant;

/** Public API representation of a stored operator decision. */
public record OperatorDecisionResponse(
        Long id,
        String decisionId,
        String alertId,
        String sensorId,
        String action,
        String routingKey,
        String status,
        String payload,
        Instant decidedAt) {

    public static OperatorDecisionResponse from(OperatorDecision decision) {
        return new OperatorDecisionResponse(decision.getId(), decision.getDecisionId(),
                decision.getAlertId(), decision.getSensorId(), decision.getAction(),
                decision.getRoutingKey(), decision.getStatus(), decision.getPayload(),
                decision.getDecidedAt());
    }
}
