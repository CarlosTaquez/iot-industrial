package com.example.iot.alertdetector;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorVibrationWindowTest {
    private final CriticityStrategy strategy = new ThresholdCriticityStrategy();

    @Test
    void detectsCriticalReadingImmediately() {
        SensorVibrationWindow sensor = new SensorVibrationWindow("sensor_A");
        assertEquals(SensorStatus.CRITICAL, sensor.registerReading(91, strategy));
    }

    @Test
    void raisesWarningAfterThreeConsecutiveHighReadings() {
        SensorVibrationWindow sensor = new SensorVibrationWindow("sensor_A");
        sensor.registerReading(80, strategy);
        sensor.registerReading(81, strategy);
        assertEquals(SensorStatus.WARNING, sensor.registerReading(82, strategy));
        assertTrue(sensor.warningThresholdReached());
        sensor.acknowledgeWarning();
        assertFalse(sensor.warningThresholdReached());
    }

    @Test
    void rejectsOutOfRangeReading() {
        SensorVibrationWindow sensor = new SensorVibrationWindow("sensor_A");
        assertThrows(IllegalArgumentException.class, () -> sensor.registerReading(101, strategy));
    }
}
