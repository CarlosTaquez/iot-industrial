package com.example.iot.historyservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "operator_decisions";
    public static final String HISTORY_QUEUE = "history_decisions_queue";

    @Bean
    public DirectExchange operatorDecisionsExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue historyDecisionsQueue() {
        return new Queue(HISTORY_QUEUE, true);
    }

    @Bean
    public Binding historyActuatorBinding(Queue historyDecisionsQueue, DirectExchange operatorDecisionsExchange) {
        return BindingBuilder.bind(historyDecisionsQueue).to(operatorDecisionsExchange).with("actuator");
    }

    @Bean
    public Binding historyMaintenanceBinding(Queue historyDecisionsQueue, DirectExchange operatorDecisionsExchange) {
        return BindingBuilder.bind(historyDecisionsQueue).to(operatorDecisionsExchange).with("maintenance");
    }
}