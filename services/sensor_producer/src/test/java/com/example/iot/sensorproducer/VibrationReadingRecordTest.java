package com.example.iot.sensorproducer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class VibrationReadingRecordTest {
    @Test
    void acceptsValidReading() {
        assertEquals("sensor_A", new VibrationReadingRecord("sensor_A", 95, "2026-11-19T12:00:00Z").sensorId());
    }

    @Test
    void rejectsInvalidSensorAndVibration() {
        assertThrows(IllegalArgumentException.class, () -> new VibrationReadingRecord(" ", 20, "2026-11-19T12:00:00Z"));
        assertThrows(IllegalArgumentException.class, () -> new VibrationReadingRecord("sensor_A", 101, "2026-11-19T12:00:00Z"));
    }
}
