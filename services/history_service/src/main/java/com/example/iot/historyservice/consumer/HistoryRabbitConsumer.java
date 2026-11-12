package com.example.iot.historyservice.consumer;

import com.example.iot.historyservice.config.RabbitConfig;
import com.example.iot.historyservice.entity.OperatorDecision;
import com.example.iot.historyservice.repository.OperatorDecisionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

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
    public void onDecision(String body,
                           @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            JsonNode node = mapper.readTree(body);
            String action = JsonFields.text(node, "action");
            String sensorId = JsonFields.text(node, "sensor_id", "sensorId", "sensor");
            decisions.save(new OperatorDecision(
                    sensorId, action != null ? action : "UNKNOWN", routingKey, body));
        } catch (Exception e) {
            log.error("Error guardando decision: {}", body, e);
        }
    }
}