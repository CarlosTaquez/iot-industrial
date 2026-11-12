package com.example.iot.actuatorworker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ActuatorDecisionConsumer {

    private final ObjectMapper objectMapper;

    public ActuatorDecisionConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "actuator_actions_queue")
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
