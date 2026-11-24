package com.example.iot.actiondispatcher.persistence.repository;

import com.example.iot.actiondispatcher.persistence.entity.AlertRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
    Optional<AlertRecord> findByAlertId(String alertId);
}
