package com.example.iot.actiondispatcher.messaging.event;

import com.example.iot.actiondispatcher.domain.model.AlertId;
import com.example.iot.actiondispatcher.domain.model.DecisionAction;
import com.example.iot.actiondispatcher.domain.model.MaintenanceDecision;
import com.example.iot.actiondispatcher.domain.model.SensorId;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DecisionEventRecord(
        @JsonProperty("decision_id") String decisionId,
        @JsonProperty("alert_id") String alertId,
        @JsonProperty("sensor_id") String sensorId,
        String action) {
    public DecisionEventRecord {
        if (decisionId == null || decisionId.isBlank()) throw new IllegalArgumentException("decision_id es obligatorio");
        new AlertId(alertId);
        new SensorId(sensorId);
        DecisionAction.from(action);
    }

    public static DecisionEventRecord from(MaintenanceDecision decision) {
        return new DecisionEventRecord(decision.decisionId(), decision.alertId(), decision.sensorId(), decision.action().name());
    }
}
