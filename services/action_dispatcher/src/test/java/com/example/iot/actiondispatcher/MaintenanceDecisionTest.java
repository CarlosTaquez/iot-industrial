package com.example.iot.actiondispatcher;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MaintenanceDecisionTest {
    @Test
    void validatesAndAllowsOnlyOneDispatchTransition() {
        MaintenanceDecision decision = new MaintenanceDecision("a-1", "sensor_A", DecisionAction.APAGADO_INMEDIATO);
        assertEquals("actuator", decision.action().routingKey());
        assertEquals(MaintenanceDecision.DecisionStatus.RECEIVED, decision.status());
        decision.markQueued();
        decision.markDispatched();
        assertEquals(MaintenanceDecision.DecisionStatus.DISPATCHED, decision.status());
        assertThrows(IllegalStateException.class, decision::markDispatched);
    }

    @Test
    void rejectsUnknownAction() {
        assertThrows(IllegalArgumentException.class, () -> DecisionAction.from("DELETE_EQUIPMENT"));
    }
}
