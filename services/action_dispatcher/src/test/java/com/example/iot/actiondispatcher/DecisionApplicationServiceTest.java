package com.example.iot.actiondispatcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecisionApplicationServiceTest {
    @Mock AlertRecordRepository alerts;
    @Mock DecisionAuditRepository decisions;
    @Mock AlertResolutionRepository resolutions;
    @Mock DecisionOutboxRepository outbox;
    DecisionApplicationService service;
    AlertRecord alertRecord;

    @BeforeEach
    void setUp() {
        service = new DecisionApplicationService(alerts, decisions, resolutions, outbox, new ObjectMapper());
        alertRecord = new AlertRecord("alert-1", "sensor_A", "CRITICAL");
        when(alerts.findByAlertId("alert-1")).thenReturn(Optional.of(alertRecord));
    }

    @Test
    void validatesAgainstStoredAlertAndPersistsBeforeDispatch() {
        when(resolutions.existsById("alert-1")).thenReturn(false);
        when(decisions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resolutions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        MaintenanceDecision result = service.decide(new OperatorDecision("alert-1", "sensor_A", "APAGADO_INMEDIATO"));
        assertEquals(MaintenanceDecision.DecisionStatus.QUEUED, result.status());
        assertEquals("RESOLVED", alertRecord.status());
        verify(resolutions).save(any(AlertResolution.class));
        verify(outbox).save(any(DecisionOutboxRecord.class));
    }

    @Test
    void rejectsActionThatDoesNotMatchAlertSeverity() {
        assertThrows(IllegalArgumentException.class, () -> service.decide(
                new OperatorDecision("alert-1", "sensor_A", "PROGRAMAR_MANTENIMIENTO_AHORA")));
        verifyNoInteractions(outbox);
    }

    @Test
    void preventsASecondDecisionForResolvedAlert() {
        when(resolutions.existsById("alert-1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.decide(
                new OperatorDecision("alert-1", "sensor_A", "APAGADO_INMEDIATO")));
        verifyNoInteractions(outbox);
    }
}
