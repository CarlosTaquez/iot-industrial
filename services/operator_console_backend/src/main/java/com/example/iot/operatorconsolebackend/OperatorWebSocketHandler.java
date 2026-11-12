package com.example.iot.operatorconsolebackend;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OperatorWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    private final PendingAlertStore pendingAlertStore;

    public OperatorWebSocketHandler(PendingAlertStore pendingAlertStore) {
        this.pendingAlertStore = pendingAlertStore;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {

        sessions.add(session);

        System.out.println(
                "[WEBSOCKET] Operador conectado: " + session.getId()
        );

        // Recuperar alertas almacenadas
        List<String> pendingAlerts =
                pendingAlertStore.getPendingAlerts();

        for (String alert : pendingAlerts) {
            sendAlert(session, alert);
        }

        System.out.println(
                "[WEBSOCKET] Alertas pendientes enviadas: "
                + pendingAlerts.size()
        );
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {

        sessions.remove(session);

        System.out.println(
                "[WEBSOCKET] Operador desconectado: " + session.getId()
        );
    }

    public void broadcastAlert(String alertJson) {

        for (WebSocketSession session : sessions) {
            sendAlert(session, alertJson);
        }
    }

    private void sendAlert(WebSocketSession session, String alertJson) {

        if (session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(alertJson));
                }
            } catch (IOException e) {
                System.err.println(
                        "[WEBSOCKET] Error enviando alerta: "
                        + e.getMessage()
                );
            }
        }
    }
}
