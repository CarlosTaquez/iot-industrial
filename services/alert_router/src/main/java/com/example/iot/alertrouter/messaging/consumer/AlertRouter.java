
package com.example.iot.alertrouter.messaging.consumer;

import com.example.iot.alertrouter.config.RabbitMQConfig;
import com.example.iot.alertrouter.domain.model.AlertSeverity;
import com.example.iot.alertrouter.domain.model.IndustrialAlert;
import com.example.iot.alertrouter.messaging.event.HumanAlertRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

            IndustrialAlert domainAlert = new IndustrialAlert(
                    alert.path("alert_id").asText(),
                    alert.path("sensor_id").asText(),
                    AlertSeverity.from(alert.path("type").asText()),
                    alert.path("vibration").asDouble(Double.NaN),
                    alert.path("timestamp").asText());

            HumanAlertRecord routedAlert = HumanAlertRecord.from(domainAlert);
            String json = objectMapper.writeValueAsString(routedAlert);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "",
                    json
            );

            System.out.println(
                    "[RABBITMQ] Alerta " + domainAlert.severity() +
                    " enviada: " + domainAlert.sensorId()
            );

        } catch (Exception e) {
            System.err.println(
                    "Error procesando alerta: " + e.getMessage()
            );
        }
    }
}
