package com.example.iot.actiondispatcher.messaging.outbox;

import com.example.iot.actiondispatcher.persistence.entity.DecisionOutboxRecord;
import com.example.iot.actiondispatcher.persistence.repository.DecisionAuditRepository;
import com.example.iot.actiondispatcher.persistence.repository.DecisionOutboxRepository;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DecisionOutboxRelay {
    private static final Logger log = LoggerFactory.getLogger(DecisionOutboxRelay.class);
    private final DecisionOutboxRepository outbox;
    private final DecisionAuditRepository audits;
    private final RabbitTemplate rabbitTemplate;

    public DecisionOutboxRelay(DecisionOutboxRepository outbox,
                               DecisionAuditRepository audits,
                               RabbitTemplate rabbitTemplate) {
        this.outbox = outbox;
        this.audits = audits;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${decision.outbox.poll-ms:1000}")
    @Transactional
    public void relayPending() {
        for (DecisionOutboxRecord record : outbox.lockPending(PageRequest.of(0, 50))) {
            relayOne(record.id());
        }
    }

    @Transactional
    public void relayOne(Long id) {
        DecisionOutboxRecord record = outbox.findById(id).orElse(null);
        if (record == null) return;
        try {
            CorrelationData correlation = new CorrelationData(record.decisionId() + ":" + record.id());
            rabbitTemplate.convertAndSend(record.exchangeName(), record.routingKey(), record.payload(), message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setMessageId(record.decisionId());
                if (record.delayMs() > 0) message.getMessageProperties().setHeader("x-delay", record.delayMs());
                return message;
            }, correlation);
            CorrelationData.Confirm confirm = correlation.getFuture().get(10, TimeUnit.SECONDS);
            if (!confirm.isAck()) throw new IllegalStateException("RabbitMQ rechazó la decisión: " + confirm.getReason());
            if (correlation.getReturned() != null) {
                throw new IllegalStateException("RabbitMQ no encontró una ruta para la decisión");
            }
            record.markPublished();
            outbox.save(record);
            audits.findByDecisionId(record.decisionId()).ifPresent(audit -> {
                audit.markDispatched();
                audits.save(audit);
            });
        } catch (Exception exception) {
            record.markFailed(exception);
            outbox.save(record);
            log.warn("No se pudo despachar decision {}; se reintentara", record.decisionId(), exception);
        }
    }
}
