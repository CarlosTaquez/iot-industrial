package com.example.iot.actuatorworker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ActuatorDecisionConsumer {

    private final ObjectMapper objectMapper;
    private final CommandReceiptRepository receipts;

    public ActuatorDecisionConsumer(ObjectMapper objectMapper, CommandReceiptRepository receipts) {
        this.objectMapper = objectMapper;
        this.receipts = receipts;
    }

    @RabbitListener(queues = "actuator_actions_queue")
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
        String receiptId = "actuator:" + decisionId;
        if (receipts.existsById(receiptId)) return;
        receipts.saveAndFlush(new CommandReceipt(decisionId));

        System.out.println(
                "[RABBITMQ] Decision recibida: " + message
        );

        switch (action) {

            case "APAGADO_INMEDIATO":

                System.out.println(
                        "[ACTUADOR] Apagado inmediato simulado del equipo: "
                                + sensorId
                );

                break;

            case "IGNORAR_10_MINUTOS":

                System.out.println(
                        "[ACTUADOR] Se registra una pausa de 10 minutos para: "
                                + sensorId
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Accion no reconocida: " + action
                );
        }

        System.out.println(
                "[ACTUADOR] Decision procesada para alerta: " + alertId
        );
    }
}
