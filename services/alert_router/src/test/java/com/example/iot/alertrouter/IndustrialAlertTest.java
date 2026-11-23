package com.example.iot.alertrouter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IndustrialAlertTest {
    @Test
    void exposesOnlyActionsForSeverityAndResolvesOnce() {
        IndustrialAlert alert = new IndustrialAlert("a-1", "sensor_A", AlertSeverity.CRITICAL, 95, "2026-11-19T00:00:00Z");
        assertEquals(2, alert.availableActions().size());
        assertThrows(IllegalArgumentException.class, () -> alert.resolveWith("PROGRAMAR_MANTENIMIENTO_AHORA"));
        alert.resolveWith("APAGADO_INMEDIATO");
        assertEquals(IndustrialAlert.AlertLifecycle.RESOLVED, alert.lifecycle());
        assertThrows(IllegalStateException.class, () -> alert.resolveWith("APAGADO_INMEDIATO"));
    }
}
