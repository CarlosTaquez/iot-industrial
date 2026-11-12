package com.example.iot.maintenanceworker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceDecisionConsumer {

    private final ObjectMapper objectMapper;

    public MaintenanceDecisionConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "maintenance_actions_queue")
    public void receiveDecision(String message) throws Exception {

        JsonNode decision = objectMapper.readTree(message);

        String alertId = decision.path("alert_id").asText();
        String sensorId = decision.path("sensor_id").asText();
        String action = decision.path("action").asText();

        if (alertId.isBlank() || sensorId.isBlank()) {
            throw new IllegalArgumentException(
                "La decision no contiene una alerta o sensor valido"
            );
        }

        System.out.println("[RABBITMQ] Decision recibida: " + message);

        switch (action) {

            case "PROGRAMAR_MANTENIMIENTO_AHORA":
                System.out.println(
                    "[MANTENIMIENTO] Solicitud de mantenimiento inmediato para: "
                    + sensorId
                );
                break;

            case "RECONOCER_Y_ESPERAR_24H":
                System.out.println(
                    "[MANTENIMIENTO] Alerta reconocida. Espera de 24 horas para: "
                    + sensorId
                );
                break;

            default:
                throw new IllegalArgumentException(
                    "Accion no reconocida: " + action
                );
        }

        System.out.println(
            "[MANTENIMIENTO] Decision procesada para alerta: " + alertId
        );
    }
}
