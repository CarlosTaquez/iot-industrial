package com.example.iot.actiondispatcher.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "operator_decisions";

    public static final String ACTUATOR_QUEUE = "actuator_actions_queue";

    public static final String MAINTENANCE_QUEUE = "maintenance_actions_queue";
    public static final String DELAYED_EXCHANGE = "delayed_maintenance";
    public static final String HISTORY_QUEUE = "history_decisions_queue";

    @Bean
    public DirectExchange operatorDecisionsExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public CustomExchange delayedMaintenanceExchange() {
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false,
                java.util.Map.of("x-delayed-type", "direct"));
    }

    @Bean
    public Queue actuatorQueue() {
        return new Queue(ACTUATOR_QUEUE, true);
    }

    @Bean
    public Queue maintenanceQueue() {
        return new Queue(MAINTENANCE_QUEUE, true);
    }

    @Bean
    public Queue historyDecisionsQueue() { return new Queue(HISTORY_QUEUE, true); }

    @Bean
    public Binding actuatorBinding(
            Queue actuatorQueue,
            DirectExchange operatorDecisionsExchange) {

        return BindingBuilder.bind(actuatorQueue)
                .to(operatorDecisionsExchange)
                .with("actuator");
    }

    @Bean
    public Binding maintenanceBinding(
            Queue maintenanceQueue,
            DirectExchange operatorDecisionsExchange) {

        return BindingBuilder.bind(maintenanceQueue)
                .to(operatorDecisionsExchange)
                .with("maintenance");
    }

    @Bean
    public Binding delayedMaintenanceBinding(Queue maintenanceQueue, CustomExchange delayedMaintenanceExchange) {
        return BindingBuilder.bind(maintenanceQueue).to(delayedMaintenanceExchange).with("maintenance").noargs();
    }

    @Bean
    public Binding auditBinding(Queue historyDecisionsQueue, DirectExchange operatorDecisionsExchange) {
        return BindingBuilder.bind(historyDecisionsQueue).to(operatorDecisionsExchange).with("audit");
    }
}