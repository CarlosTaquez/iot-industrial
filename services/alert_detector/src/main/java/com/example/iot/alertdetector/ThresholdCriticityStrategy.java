package com.example.iot.alertdetector;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ThresholdCriticityStrategy implements CriticityStrategy {
    @Override
    public SensorStatus evaluate(double latestReading, List<Double> recentReadings) {
        if (latestReading > 90) return SensorStatus.CRITICAL;
        if (recentReadings.size() == 3 && recentReadings.stream().allMatch(value -> value > 75)) {
            return SensorStatus.WARNING;
        }
        return SensorStatus.NORMAL;
    }
}
