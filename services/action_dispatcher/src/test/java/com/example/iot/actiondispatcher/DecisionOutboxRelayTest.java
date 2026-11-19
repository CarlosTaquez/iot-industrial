package com.example.iot.actiondispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class DecisionOutboxRelayTest {
    @Mock DecisionOutboxRepository outbox;
    @Mock DecisionAuditRepository audits;
    @Mock RabbitTemplate rabbitTemplate;

    @Test
    void sendsDelayedActionWithTwentyFourHourHeaderAndMarksOutboxPublished() {
        MaintenanceDecision decision = new MaintenanceDecision("alert-1", "sensor_A", DecisionAction.RECONOCER_Y_ESPERAR_24H);
        DecisionOutboxRecord record = new DecisionOutboxRecord(decision, "{\"decision_id\":\"id-1\"}");
        when(outbox.findById(1L)).thenReturn(Optional.of(record));
        when(audits.findByDecisionId(decision.decisionId())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(4);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString(),
                any(MessagePostProcessor.class), any(CorrelationData.class));

        new DecisionOutboxRelay(outbox, audits, rabbitTemplate).relayOne(1L);

        ArgumentCaptor<MessagePostProcessor> processor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.DELAYED_EXCHANGE), eq("maintenance"),
                eq(record.payload()), processor.capture(), any(CorrelationData.class));
        Message message = processor.getValue().postProcessMessage(MessageBuilder.withBody(new byte[0]).build());
        assertEquals(24L * 60 * 60 * 1000, message.getMessageProperties().getHeaders().get("x-delay"));
        verify(outbox).save(record);
    }
}
