package com.example.iot.historyservice.repository;

import com.example.iot.historyservice.entity.Alert;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Alert> findBySensorIdOrderByCreatedAtDesc(String sensorId, Pageable pageable);
    boolean existsByAlertId(String alertId);
}