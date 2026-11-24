package com.example.iot.historyservice.messaging.consumer;

import com.example.iot.historyservice.config.RabbitConfig;
import com.example.iot.historyservice.persistence.entity.OperatorDecision;
import com.example.iot.historyservice.persistence.repository.OperatorDecisionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HistoryRabbitConsumer {

    private static final Logger log = LoggerFactory.getLogger(HistoryRabbitConsumer.class);

    private final ObjectMapper mapper;
    private final OperatorDecisionRepository decisions;

    public HistoryRabbitConsumer(ObjectMapper mapper, OperatorDecisionRepository decisions) {
        this.mapper = mapper;
        this.decisions = decisions;
    }

    @RabbitListener(queues = RabbitConfig.HISTORY_QUEUE)
    @Transactional
    public void onDecision(String body,
                           @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            JsonNode node = mapper.readTree(body);
            String action = JsonFields.text(node, "action");
            String decisionId = JsonFields.text(node, "decision_id", "decisionId");
            String sensorId = JsonFields.text(node, "sensor_id", "sensorId", "sensor");
            String alertId = JsonFields.text(node, "alert_id", "alertId");
            if (decisionId == null || sensorId == null || action == null) {
                throw new IllegalArgumentException("decision_id, sensor_id y action son obligatorios");
            }
            if (decisions.existsByDecisionId(decisionId)) return;
            decisions.save(new OperatorDecision(
                    decisionId, sensorId, action, routingKey, body, alertId));
        } catch (Exception e) {
            log.error("Error guardando decision: {}", body, e);
            throw new IllegalStateException("No se pudo persistir la auditoría de decisión", e);
        }
    }
}
