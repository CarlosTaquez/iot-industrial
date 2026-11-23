package com.example.iot.alertdetector;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class AlertDetectorTest {
    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @Mock CriticityStrategy criticityStrategy;

    @Test
    void publishesSensorStatusAndCriticalAlertToKafka() {
        when(criticityStrategy.evaluate(95.0, List.of(95.0))).thenReturn(SensorStatus.CRITICAL);
        AlertDetector detector = new AlertDetector(kafkaTemplate, new ObjectMapper(), criticityStrategy);

        detector.processSensorData("{\"sensor_id\":\"sensor_A\",\"vibration\":95}");

        verify(kafkaTemplate).send(eq("sensor_status"), eq("sensor_A"), any(SensorStatusRecord.class));
        verify(kafkaTemplate).send(eq("alerts_critical"), eq("sensor_A"), any(AlertEventRecord.class));
        verify(kafkaTemplate, never()).send(eq("alerts_warning"), eq("sensor_A"), any());
    }

    @Test
    void ignoresMalformedReadingWithoutPublishing() {
        AlertDetector detector = new AlertDetector(kafkaTemplate, new ObjectMapper(), criticityStrategy);

        detector.processSensorData("{}");

        verifyNoInteractions(kafkaTemplate, criticityStrategy);
    }
}
