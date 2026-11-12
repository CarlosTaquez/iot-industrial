package com.example.iot.plantmonitorbackend;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlantWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessions.add(session);

        System.out.println(
                "[WEBSOCKET] Dashboard conectado: " + session.getId()
        );
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {

        sessions.remove(session);

        System.out.println(
                "[WEBSOCKET] Dashboard desconectado: " + session.getId()
        );
    }

    public void broadcastStatus(String statusJson) {

        for (WebSocketSession session : sessions) {

            if (session.isOpen()) {

                try {
                    synchronized (session) {
                        session.sendMessage(
                                new TextMessage(statusJson)
                        );
                    }

                } catch (IOException e) {

                    System.err.println(
                            "[WEBSOCKET] Error enviando estado: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
}
