package com.example.iot.operatorconsolebackend;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQAlertListener {

    private final OperatorWebSocketHandler webSocketHandler;
    private final PendingAlertStore pendingAlertStore;

    public RabbitMQAlertListener(
            OperatorWebSocketHandler webSocketHandler,
            PendingAlertStore pendingAlertStore) {

        this.webSocketHandler = webSocketHandler;
        this.pendingAlertStore = pendingAlertStore;
    }

    @RabbitListener(queues = "operator_alerts_queue")
    public void receiveAlert(String message) {

        System.out.println(
                "[RABBITMQ] Alerta recibida: " + message
        );

        // Guardar la alerta temporalmente
        pendingAlertStore.addAlert(message);

        // Enviar la alerta a los operadores conectados
        webSocketHandler.broadcastAlert(message);
    }
}
