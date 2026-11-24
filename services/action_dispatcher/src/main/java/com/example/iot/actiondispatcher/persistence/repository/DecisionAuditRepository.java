package com.example.iot.actiondispatcher.persistence.repository;

import com.example.iot.actiondispatcher.persistence.entity.DecisionAuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionAuditRepository extends JpaRepository<DecisionAuditRecord, Long> {
    boolean existsByAlertId(String alertId);
    java.util.Optional<DecisionAuditRecord> findByDecisionId(String decisionId);
}
