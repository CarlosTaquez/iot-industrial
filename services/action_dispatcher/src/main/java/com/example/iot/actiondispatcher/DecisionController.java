package com.example.iot.actiondispatcher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/decide")
public class DecisionController {

    private final DecisionApplicationService decisionApplicationService;

    public DecisionController(DecisionApplicationService decisionApplicationService) {
        this.decisionApplicationService = decisionApplicationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receiveDecision(
            @RequestBody OperatorDecision decision) {

        if (decision.getAlert_id() == null
                || decision.getAlert_id().isBlank()
                || decision.getSensor_id() == null
                || decision.getSensor_id().isBlank()
                || decision.getAction() == null
                || decision.getAction().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", "Todos los campos son obligatorios"
                    )
            );
        }

        try {

            MaintenanceDecision domainDecision = decisionApplicationService.decide(decision);

            System.out.println(
                    "[HTTP] Decision recibida para sensor: "
                            + domainDecision.sensorId()
            );

            return ResponseEntity.accepted().body(
                    Map.of(
                            "status", "ACCEPTED",
                            "message", "Decision guardada y encolada para despacho",
                            "decision_id", domainDecision.decisionId(),
                            "alert_id", domainDecision.alertId(),
                            "sensor_id", domainDecision.sensorId(),
                            "action", domainDecision.action().name(),
                            "routing_key", domainDecision.action().routingKey(),
                            "decision_status", domainDecision.status().name()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    )
            );

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("status", "ERROR", "message", e.getMessage()));
        } catch (Exception e) {

            System.err.println(
                    "[HTTP] Error procesando decision: " + e.getMessage()
            );

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "status", "ERROR",
                            "message", "No se pudo procesar la decision"
                    )
            );
        }
    }
}
