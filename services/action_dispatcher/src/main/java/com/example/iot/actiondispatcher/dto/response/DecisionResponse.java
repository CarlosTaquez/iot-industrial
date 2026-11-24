package com.example.iot.actiondispatcher.dto.response;

import com.example.iot.actiondispatcher.domain.model.MaintenanceDecision;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DecisionResponse(
        String status,
        String message,
        @JsonProperty("decision_id") String decisionId,
        @JsonProperty("alert_id") String alertId,
        @JsonProperty("sensor_id") String sensorId,
        String action,
        @JsonProperty("routing_key") String routingKey,
        @JsonProperty("decision_status") String decisionStatus) {

    public static DecisionResponse accepted(MaintenanceDecision decision) {
        return new DecisionResponse(
                "ACCEPTED",
                "Decision guardada y encolada para despacho",
                decision.decisionId(),
                decision.alertId(),
                decision.sensorId(),
                decision.action().name(),
                decision.action().routingKey(),
                decision.status().name());
    }
}
