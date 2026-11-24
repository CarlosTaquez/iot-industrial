package com.example.iot.actiondispatcher.domain.model;

public final class MaintenanceDecision {
    private final String decisionId = java.util.UUID.randomUUID().toString();
    private final AlertId alertId;
    private final SensorId sensorId;
    private final DecisionAction action;
    private DecisionStatus status = DecisionStatus.RECEIVED;

    public MaintenanceDecision(String alertId, String sensorId, DecisionAction action) {
        if (action == null) throw new IllegalArgumentException("La acción es obligatoria");
        this.alertId = new AlertId(alertId);
        this.sensorId = new SensorId(sensorId);
        this.action = action;
    }

    public void markQueued() {
        if (status != DecisionStatus.RECEIVED) throw new IllegalStateException("La decisión ya se encoló");
        status = DecisionStatus.QUEUED;
    }

    public void markDispatched() {
        if (status != DecisionStatus.QUEUED) throw new IllegalStateException("La decisión no está encolada");
        status = DecisionStatus.DISPATCHED;
    }

    public String alertId() { return alertId.value(); }
    public String decisionId() { return decisionId; }
    public String sensorId() { return sensorId.value(); }
    public DecisionAction action() { return action; }
    public DecisionStatus status() { return status; }

    public enum DecisionStatus { RECEIVED, QUEUED, DISPATCHED }
}
