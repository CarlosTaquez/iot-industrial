package com.example.iot.actiondispatcher;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "operator_decisions";

    public static final String ACTUATOR_QUEUE = "actuator_actions_queue";

    public static final String MAINTENANCE_QUEUE = "maintenance_actions_queue";

    @Bean
    public DirectExchange operatorDecisionsExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
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
}
