package com.example.iot.operatorconsolebackend.application.store;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PendingAlertStore {

    private final List<String> pendingAlerts = new ArrayList<>();

    public synchronized void addAlert(String alertJson) {
        pendingAlerts.add(alertJson);
    }

    public synchronized List<String> getPendingAlerts() {
        return new ArrayList<>(pendingAlerts);
    }
}
