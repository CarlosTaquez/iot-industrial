package com.example.iot.alertdetector.domain.model;

import com.example.iot.alertdetector.domain.state.SensorState;
import com.example.iot.alertdetector.domain.strategy.CriticityStrategy;
import java.util.ArrayDeque;
import java.util.List;

public final class SensorVibrationWindow {
    private static final int CAPACITY = 3;
    private final SensorId sensorId;
    private final ArrayDeque<Double> readings = new ArrayDeque<>();
    private SensorState state = SensorState.from(SensorStatus.NORMAL);
    private boolean warningRaised;
    private boolean criticalRaised;

    public SensorVibrationWindow(String sensorId) {
        this.sensorId = new SensorId(sensorId);
    }

    public SensorStatus registerReading(double vibration, CriticityStrategy strategy) {
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) {
            throw new IllegalArgumentException("La vibración debe estar entre 0 y 100");
        }
        if (strategy == null) throw new IllegalArgumentException("La estrategia es obligatoria");
        readings.addLast(vibration);
        if (readings.size() > CAPACITY) readings.removeFirst();
        transitionTo(strategy.evaluate(vibration, List.copyOf(readings)));
        return state.status();
    }

    public boolean warningThresholdReached() {
        return state.status() == SensorStatus.WARNING && !warningRaised;
    }

    public boolean criticalThresholdReached() {
        return state.status() == SensorStatus.CRITICAL && !criticalRaised;
    }

    public void acknowledgeWarning() {
        if (state.status() != SensorStatus.WARNING) throw new IllegalStateException("No hay advertencia pendiente");
        warningRaised = true;
    }

    public void acknowledgeCritical() {
        if (state.status() != SensorStatus.CRITICAL) throw new IllegalStateException("No hay alerta crÃ­tica pendiente");
        criticalRaised = true;
    }

    public String sensorId() { return sensorId.value(); }

    private void transitionTo(SensorStatus next) {
        SensorStatus previous = state.status();
        state = state.transitionTo(next);
        if (previous != next) {
            warningRaised = false;
            criticalRaised = false;
        }
    }
}
