package com.example.iot.alertdetector;

record NormalState() implements SensorState {
    @Override public SensorStatus status() { return SensorStatus.NORMAL; }
    @Override public SensorState transitionTo(SensorStatus target) {
        return target == status() ? this : SensorState.from(target);
    }
}
