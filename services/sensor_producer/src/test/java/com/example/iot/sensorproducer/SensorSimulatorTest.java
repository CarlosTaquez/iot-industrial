package com.example.iot.sensorproducer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.iot.sensorproducer.messaging.event.VibrationReadingRecord;
import com.example.iot.sensorproducer.messaging.producer.SensorSimulator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class SensorSimulatorTest {
    @Mock KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void publishesOneValidReadingForEachSensor() {
        SensorSimulator simulator = new SensorSimulator(kafkaTemplate);

        simulator.generateSensorData();

        ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<VibrationReadingRecord> readings = ArgumentCaptor.forClass(VibrationReadingRecord.class);
        verify(kafkaTemplate, times(10)).send(eq("sensor_data"), keys.capture(), readings.capture());

        assertEquals(List.of("sensor_A", "sensor_B", "sensor_C", "sensor_D", "sensor_E",
                "sensor_F", "sensor_G", "sensor_H", "sensor_I", "sensor_J"), keys.getAllValues());
        assertTrue(readings.getAllValues().stream().allMatch(reading ->
                reading.vibration() >= 20 && reading.vibration() <= 100));
    }
}
