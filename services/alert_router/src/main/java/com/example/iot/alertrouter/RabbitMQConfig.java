
package com.example.iot.alertrouter;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "human_alerts";

    @Bean
    public FanoutExchange humanAlertsExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }
}
