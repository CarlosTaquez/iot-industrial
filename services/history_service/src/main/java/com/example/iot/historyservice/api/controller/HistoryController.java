package com.example.iot.historyservice.api.controller;

import com.example.iot.historyservice.dto.response.AlertResponse;
import com.example.iot.historyservice.dto.response.OperatorDecisionResponse;
import com.example.iot.historyservice.dto.response.SensorReadingResponse;
import com.example.iot.historyservice.persistence.entity.Alert;
import com.example.iot.historyservice.persistence.entity.OperatorDecision;
import com.example.iot.historyservice.persistence.entity.SensorReading;
import com.example.iot.historyservice.persistence.repository.AlertRepository;
import com.example.iot.historyservice.persistence.repository.OperatorDecisionRepository;
import com.example.iot.historyservice.persistence.repository.SensorReadingRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@CrossOrigin(origins = "*")
@Validated
public class HistoryController {

    private final AlertRepository alerts;
    private final OperatorDecisionRepository decisions;
    private final SensorReadingRepository readings;

    public HistoryController(AlertRepository alerts,
                             OperatorDecisionRepository decisions,
                             SensorReadingRepository readings) {
        this.alerts = alerts;
        this.decisions = decisions;
        this.readings = readings;
    }

    @GetMapping("/alerts")
    public List<AlertResponse> alerts(@RequestParam(required = false) @Size(max = 100) String sensorId,
                              @RequestParam(defaultValue = "50") @Min(1) @Max(500) int limit) {
        Pageable page = PageRequest.of(0, limit);
        List<Alert> results = sensorId == null
                ? alerts.findAllByOrderByCreatedAtDesc(page)
                : alerts.findBySensorIdOrderByCreatedAtDesc(sensorId, page);
        return results.stream().map(AlertResponse::from).toList();
    }

    @GetMapping("/decisions")
    public List<OperatorDecisionResponse> decisions(@RequestParam(required = false) @Size(max = 100) String sensorId,
                                            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int limit) {
        Pageable page = PageRequest.of(0, limit);
        List<OperatorDecision> results = sensorId == null
                ? decisions.findAllByOrderByDecidedAtDesc(page)
                : decisions.findBySensorIdOrderByDecidedAtDesc(sensorId, page);
        return results.stream().map(OperatorDecisionResponse::from).toList();
    }

    @GetMapping("/readings")
    public List<SensorReadingResponse> readings(@RequestParam(required = false) @Size(max = 100) String sensorId,
                                        @RequestParam(defaultValue = "50") @Min(1) @Max(500) int limit) {
        Pageable page = PageRequest.of(0, limit);
        List<SensorReading> results = sensorId == null
                ? readings.findAllByOrderByRecordedAtDesc(page)
                : readings.findBySensorIdOrderByRecordedAtDesc(sensorId, page);
        return results.stream().map(SensorReadingResponse::from).toList();
    }
}
