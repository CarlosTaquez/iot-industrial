package com.example.iot.historyservice.dto.response;

import com.example.iot.historyservice.persistence.entity.SensorReading;
import java.time.Instant;

/** Public API representation of a stored sensor reading. */
public record SensorReadingResponse(
        Long id,
        String sensorId,
        Double vibration,
        Instant recordedAt,
        Instant storedAt) {

    public static SensorReadingResponse from(SensorReading reading) {
        return new SensorReadingResponse(reading.getId(), reading.getSensorId(),
                reading.getVibration(), reading.getRecordedAt(), reading.getStoredAt());
    }
}
