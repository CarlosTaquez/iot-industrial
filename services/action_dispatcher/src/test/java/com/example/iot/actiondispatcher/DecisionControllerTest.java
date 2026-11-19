package com.example.iot.actiondispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DecisionControllerTest {
    @Mock DecisionApplicationService decisionApplicationService;

    @Test
    void returnsDecisionIdentifiersRouteAndQueuedStatus() {
        MaintenanceDecision queued = new MaintenanceDecision(
                "alert-1", "sensor_A", DecisionAction.APAGADO_INMEDIATO);
        queued.markQueued();
        when(decisionApplicationService.decide(any(OperatorDecision.class))).thenReturn(queued);
        DecisionController controller = new DecisionController(decisionApplicationService);

        ResponseEntity<Map<String, String>> response = controller.receiveDecision(
                new OperatorDecision("alert-1", "sensor_A", "APAGADO_INMEDIATO"));

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(queued.decisionId(), response.getBody().get("decision_id"));
        assertEquals("alert-1", response.getBody().get("alert_id"));
        assertEquals("actuator", response.getBody().get("routing_key"));
        assertEquals("QUEUED", response.getBody().get("decision_status"));
        verify(decisionApplicationService).decide(any(OperatorDecision.class));
    }
}
