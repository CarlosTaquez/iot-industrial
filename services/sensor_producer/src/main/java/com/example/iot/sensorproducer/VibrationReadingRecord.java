package com.example.iot.sensorproducer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/** Immutable Kafka contract. Validation fails before publishing invalid telemetry. */
public record VibrationReadingRecord(
        @JsonProperty("sensor_id") String sensorId,
        double vibration,
        String timestamp) {
    public VibrationReadingRecord {
        new SensorId(sensorId);
        if (!Double.isFinite(vibration) || vibration < 0 || vibration > 100) throw new IllegalArgumentException("vibration debe estar entre 0 y 100");
        if (timestamp == null || timestamp.isBlank()) timestamp = Instant.now().toString();
        Instant.parse(timestamp);
    }
}
