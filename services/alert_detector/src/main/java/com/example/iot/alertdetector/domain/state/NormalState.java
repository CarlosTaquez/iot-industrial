package com.example.iot.alertdetector.domain.state;

import com.example.iot.alertdetector.domain.model.SensorStatus;

record NormalState() implements SensorState {
    @Override public SensorStatus status() { return SensorStatus.NORMAL; }
    @Override public SensorState transitionTo(SensorStatus target) {
        return target == status() ? this : SensorState.from(target);
    }
}
