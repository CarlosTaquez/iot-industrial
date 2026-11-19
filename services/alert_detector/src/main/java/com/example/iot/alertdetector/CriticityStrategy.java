package com.example.iot.alertdetector;

import java.util.List;

public interface CriticityStrategy {
    SensorStatus evaluate(double latestReading, List<Double> recentReadings);
}
