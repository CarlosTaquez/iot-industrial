package com.example.iot.actiondispatcher.application;

import com.example.iot.actiondispatcher.application.command.OperatorDecision;
import com.example.iot.actiondispatcher.domain.model.DecisionAction;
import com.example.iot.actiondispatcher.domain.model.MaintenanceDecision;
import com.example.iot.actiondispatcher.messaging.event.DecisionEventRecord;
import com.example.iot.actiondispatcher.persistence.entity.AlertRecord;
import com.example.iot.actiondispatcher.persistence.entity.AlertResolution;
import com.example.iot.actiondispatcher.persistence.entity.DecisionAuditRecord;
import com.example.iot.actiondispatcher.persistence.entity.DecisionOutboxRecord;
import com.example.iot.actiondispatcher.persistence.repository.AlertRecordRepository;
import com.example.iot.actiondispatcher.persistence.repository.AlertResolutionRepository;
import com.example.iot.actiondispatcher.persistence.repository.DecisionAuditRepository;
import com.example.iot.actiondispatcher.persistence.repository.DecisionOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecisionApplicationService {
    private final AlertRecordRepository alerts;
    private final DecisionAuditRepository decisions;
    private final AlertResolutionRepository resolutions;
    private final DecisionOutboxRepository outbox;
    private final ObjectMapper objectMapper;

    public DecisionApplicationService(AlertRecordRepository alerts,
                                      DecisionAuditRepository decisions,
                                      AlertResolutionRepository resolutions,
                                      DecisionOutboxRepository outbox,
                                      ObjectMapper objectMapper) {
        this.alerts = alerts;
        this.decisions = decisions;
        this.resolutions = resolutions;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MaintenanceDecision decide(OperatorDecision request) {
        AlertRecord alert = alerts.findByAlertId(request.getAlert_id())
                .orElseThrow(() -> new IllegalArgumentException("No existe la alerta indicada"));
        if (!alert.sensorId().equals(request.getSensor_id())) {
            throw new IllegalArgumentException("La alerta no pertenece al sensor indicado");
        }
        DecisionAction action = DecisionAction.from(request.getAction());
        if (!action.isAllowedFor(alert.severity())) {
            throw new IllegalArgumentException("La acción no corresponde a la severidad de la alerta");
        }
        alert.resolveWith(action);
        if (resolutions.existsById(alert.alertId())) {
            throw new IllegalStateException("La alerta ya tiene una decisión registrada");
        }

        MaintenanceDecision decision = new MaintenanceDecision(alert.alertId(), alert.sensorId(), action);
        resolutions.save(new AlertResolution(alert.alertId(), decision.decisionId()));
        alerts.save(alert);
        decisions.save(new DecisionAuditRecord(decision, serialize(decision)));
        outbox.save(new DecisionOutboxRecord(decision, serialize(decision)));
        decision.markQueued();
        return decision;
    }

    private String serialize(MaintenanceDecision decision) {
        try {
            return objectMapper.writeValueAsString(DecisionEventRecord.from(decision));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar la decisión", exception);
        }
    }
}
