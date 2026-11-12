package com.example.iot.historyservice.repository;

import com.example.iot.historyservice.entity.SensorReading;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findAllByOrderByRecordedAtDesc(Pageable pageable);
    List<SensorReading> findBySensorIdOrderByRecordedAtDesc(String sensorId, Pageable pageable);
}