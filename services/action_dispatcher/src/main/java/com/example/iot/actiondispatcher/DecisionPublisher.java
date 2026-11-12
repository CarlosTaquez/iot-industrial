package com.example.iot.actiondispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DecisionPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DecisionPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDecision(OperatorDecision decision) {

        String routingKey = getRoutingKey(decision.getAction());

        try {

            String message = objectMapper.writeValueAsString(decision);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    message,
                    rabbitMessage -> {
                        rabbitMessage.getMessageProperties()
                                .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                        return rabbitMessage;
                    }
            );

            System.out.println(
                    "[RABBITMQ] Decision publicada: " + message
            );

            System.out.println(
                    "[RABBITMQ] Routing key: " + routingKey
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Error convirtiendo la decision a JSON", e
            );
        }
    }

    private String getRoutingKey(String action) {

        if (action == null) {
            throw new IllegalArgumentException(
                    "La accion no puede estar vacia"
            );
        }

        return switch (action) {

            case "APAGADO_INMEDIATO",
                 "IGNORAR_10_MINUTOS" -> "actuator";

            case "PROGRAMAR_MANTENIMIENTO_AHORA",
                 "RECONOCER_Y_ESPERAR_24H" -> "maintenance";

            default -> throw new IllegalArgumentException(
                    "Accion no permitida: " + action
            );
        };
    }
}
