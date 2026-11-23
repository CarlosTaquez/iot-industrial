package com.example.iot.actiondispatcher.dto.request;

import com.example.iot.actiondispatcher.OperatorDecision;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DecisionRequest(
        @NotBlank(message = "alert_id es obligatorio")
        @JsonProperty("alert_id") String alertId,
        @NotBlank(message = "sensor_id es obligatorio")
        @JsonProperty("sensor_id") String sensorId,
        @NotBlank(message = "action es obligatorio") String action) {

    public OperatorDecision toOperatorDecision() {
        return new OperatorDecision(alertId, sensorId, action);
    }
}
