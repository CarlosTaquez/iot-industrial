package com.example.iot.actiondispatcher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionAuditRepository extends JpaRepository<DecisionAuditRecord, Long> {
    boolean existsByAlertId(String alertId);
    java.util.Optional<DecisionAuditRecord> findByDecisionId(String decisionId);
}
