package com.example.iot.alertdetector.domain.state;

import com.example.iot.alertdetector.domain.model.SensorStatus;

public sealed interface SensorState permits NormalState, WarningState, CriticalState {
    SensorStatus status();
    SensorState transitionTo(SensorStatus target);

    static SensorState from(SensorStatus status) {
        return switch (status) {
            case NORMAL -> new NormalState();
            case WARNING -> new WarningState();
            case CRITICAL -> new CriticalState();
        };
    }
}
