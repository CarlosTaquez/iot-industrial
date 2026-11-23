package com.example.iot.actiondispatcher;

import com.example.iot.actiondispatcher.dto.request.DecisionRequest;
import com.example.iot.actiondispatcher.dto.response.DecisionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decide")
public class DecisionController {

    private final DecisionApplicationService decisionApplicationService;

    public DecisionController(DecisionApplicationService decisionApplicationService) {
        this.decisionApplicationService = decisionApplicationService;
    }

    @PostMapping
    public ResponseEntity<DecisionResponse> receiveDecision(@Valid @RequestBody DecisionRequest request) {
        MaintenanceDecision decision = decisionApplicationService.decide(request.toOperatorDecision());
        return ResponseEntity.accepted().body(DecisionResponse.accepted(decision));
    }
}
