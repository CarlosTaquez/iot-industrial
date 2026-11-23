package com.example.iot.actiondispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.iot.actiondispatcher.dto.request.DecisionRequest;
import com.example.iot.actiondispatcher.dto.response.DecisionResponse;
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

        ResponseEntity<?> response = controller.receiveDecision(
                new DecisionRequest("alert-1", "sensor_A", "APAGADO_INMEDIATO"));

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        DecisionResponse body = (DecisionResponse) response.getBody();
        assertEquals(queued.decisionId(), body.decisionId());
        assertEquals("alert-1", body.alertId());
        assertEquals("actuator", body.routingKey());
        assertEquals("QUEUED", body.decisionStatus());
        verify(decisionApplicationService).decide(any(OperatorDecision.class));
    }
}
