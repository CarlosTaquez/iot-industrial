package com.example.iot.plantmonitorbackend;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PlantKafkaConsumer {

    private final PlantWebSocketHandler webSocketHandler;

    public PlantKafkaConsumer(PlantWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(
            topics = "sensor_status",
            groupId = "plant-monitor-status-group"
    )
    public void receiveSensorStatus(String message) {

        System.out.println(
                "[KAFKA] Estado de sensor recibido: " + message
        );

        webSocketHandler.broadcastStatus(message);
    }

    @KafkaListener(
            topics = {"alerts_critical", "alerts_warning"},
            groupId = "plant-monitor-alerts-group"
    )
    public void receiveSensorAlert(String message) {

        System.out.println(
                "[KAFKA] Alerta de sensor recibida: " + message
        );

        webSocketHandler.broadcastStatus(message);
    }
}
