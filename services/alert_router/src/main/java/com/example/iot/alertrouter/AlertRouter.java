
package com.example.iot.alertrouter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AlertRouter {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AlertRouter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = {"alerts_critical", "alerts_warning"},
            groupId = "alert-router-group"
    )
    public void routeAlert(String message) {

        try {
            JsonNode alert = objectMapper.readTree(message);

            String alertId = alert.get("alert_id").asText();
            String sensorId = alert.get("sensor_id").asText();
            String type = alert.get("type").asText();

            List<String> actions;

            if ("CRITICAL".equals(type)) {

                actions = List.of(
                        "APAGADO_INMEDIATO",
                        "IGNORAR_10_MINUTOS"
                );

            } else if ("WARNING".equals(type)) {

                actions = List.of(
                        "PROGRAMAR_MANTENIMIENTO_AHORA",
                        "RECONOCER_Y_ESPERAR_24H"
                );

            } else {
                System.err.println(
                        "Tipo de alerta desconocido: " + type
                );
                return;
            }

            Map<String, Object> routedAlert =
                    new LinkedHashMap<>();

            routedAlert.put("alert_id", alertId);
            routedAlert.put("sensor_id", sensorId);
            routedAlert.put("type", type);
            routedAlert.put("vibration",
                    alert.get("vibration").asDouble());
            routedAlert.put("timestamp",
                    alert.get("timestamp").asText());
            routedAlert.put("actions", actions);

            String json = objectMapper.writeValueAsString(routedAlert);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "",
                    json
            );

            System.out.println(
                    "[RABBITMQ] Alerta " + type +
                    " enviada: " + sensorId
            );

        } catch (Exception e) {
            System.err.println(
                    "Error procesando alerta: " + e.getMessage()
            );
        }
    }
}
