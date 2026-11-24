package com.example.iot.alertdetector.domain.state;

import com.example.iot.alertdetector.domain.model.SensorStatus;

record CriticalState() implements SensorState {
    @Override public SensorStatus status() { return SensorStatus.CRITICAL; }
    @Override public SensorState transitionTo(SensorStatus target) {
        return target == status() ? this : SensorState.from(target);
    }
}
