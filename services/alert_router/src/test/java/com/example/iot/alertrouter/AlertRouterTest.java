package com.example.iot.alertrouter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.iot.alertrouter.config.RabbitMQConfig;
import com.example.iot.alertrouter.messaging.consumer.AlertRouter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class AlertRouterTest {
    @Mock RabbitTemplate rabbitTemplate;

    @Test
    void routesValidCriticalAlertToOperatorExchange() throws Exception {
        AlertRouter router = new AlertRouter(rabbitTemplate);
        String event = """
                {"alert_id":"alert-1","sensor_id":"sensor_A","type":"CRITICAL",
                 "vibration":95,"timestamp":"2026-11-19T12:00:00Z"}
                """;

        router.routeAlert(event);

        ArgumentCaptor<String> routedJson = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EXCHANGE_NAME), eq(""), routedJson.capture());
        JsonNode routedAlert = new ObjectMapper().readTree(routedJson.getValue());
        assertEquals("alert-1", routedAlert.path("alert_id").asText());
        assertEquals("CRITICAL", routedAlert.path("type").asText());
        assertEquals(2, routedAlert.path("actions").size());
    }

    @Test
    void doesNotPublishMalformedAlert() {
        new AlertRouter(rabbitTemplate).routeAlert("not-json");

        verifyNoInteractions(rabbitTemplate);
    }
}
