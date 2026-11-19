package com.example.iot.historyservice.repository;

import com.example.iot.historyservice.entity.OperatorDecision;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorDecisionRepository extends JpaRepository<OperatorDecision, Long> {
    boolean existsByDecisionId(String decisionId);
    List<OperatorDecision> findAllByOrderByDecidedAtDesc(Pageable pageable);
    List<OperatorDecision> findBySensorIdOrderByDecidedAtDesc(String sensorId, Pageable pageable);
}
