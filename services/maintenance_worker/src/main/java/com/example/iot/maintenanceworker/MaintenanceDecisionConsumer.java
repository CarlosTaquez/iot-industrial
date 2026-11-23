package com.example.iot.maintenanceworker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class MaintenanceDecisionConsumer {

    private final ObjectMapper objectMapper;
    private final CommandReceiptRepository receipts;

    public MaintenanceDecisionConsumer(ObjectMapper objectMapper, CommandReceiptRepository receipts) {
        this.objectMapper = objectMapper;
        this.receipts = receipts;
    }

    @RabbitListener(queues = "maintenance_actions_queue")
    @Transactional
    public void receiveDecision(String message) throws Exception {

        JsonNode decision = objectMapper.readTree(message);

        String alertId = decision.path("alert_id").asText();
        String sensorId = decision.path("sensor_id").asText();
        String action = decision.path("action").asText();
        String decisionId = decision.path("decision_id").asText();

        if (alertId.isBlank() || sensorId.isBlank()) {
            throw new IllegalArgumentException(
                "La decision no contiene una alerta o sensor valido"
            );
        }
        if (decisionId.isBlank()) decisionId = UUID.nameUUIDFromBytes((alertId + ":" + action).getBytes(StandardCharsets.UTF_8)).toString();
        String receiptId = "maintenance:" + decisionId;
        if (receipts.existsById(receiptId)) return;
        receipts.saveAndFlush(new CommandReceipt(decisionId));

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
                    "[MANTENIMIENTO] Decisión recibida tras el retardo de 24 horas para: "
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
