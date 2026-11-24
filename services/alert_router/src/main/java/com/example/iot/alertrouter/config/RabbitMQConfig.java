
package com.example.iot.alertrouter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "human_alerts";
    public static final String OPERATOR_ALERTS_QUEUE = "operator_alerts_queue";

    @Bean
    public FanoutExchange humanAlertsExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue operatorAlertsQueue() {
        return new Queue(OPERATOR_ALERTS_QUEUE, true);
    }

    @Bean
    public Binding operatorAlertsBinding(
            Queue operatorAlertsQueue,
            FanoutExchange humanAlertsExchange) {

        return BindingBuilder.bind(operatorAlertsQueue)
                .to(humanAlertsExchange);
    }
}
