package com.example.iot.alertdetector.domain.strategy;

import com.example.iot.alertdetector.domain.model.SensorStatus;
import java.util.List;

public interface CriticityStrategy {
    SensorStatus evaluate(double latestReading, List<Double> recentReadings);
}
