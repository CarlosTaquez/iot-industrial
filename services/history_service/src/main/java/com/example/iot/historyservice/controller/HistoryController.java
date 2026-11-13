package com.example.iot.historyservice.controller;

import com.example.iot.historyservice.entity.Alert;
import com.example.iot.historyservice.entity.OperatorDecision;
import com.example.iot.historyservice.entity.SensorReading;
import com.example.iot.historyservice.repository.AlertRepository;
import com.example.iot.historyservice.repository.OperatorDecisionRepository;
import com.example.iot.historyservice.repository.SensorReadingRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@CrossOrigin(origins = "*")
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
    public List<Alert> alerts(@RequestParam(required = false) String sensorId,
                              @RequestParam(defaultValue = "50") int limit) {
        Pageable page = PageRequest.of(0, clamp(limit));
        return sensorId == null
                ? alerts.findAllByOrderByCreatedAtDesc(page)
                : alerts.findBySensorIdOrderByCreatedAtDesc(sensorId, page);
    }

    @GetMapping("/decisions")
    public List<OperatorDecision> decisions(@RequestParam(required = false) String sensorId,
                                            @RequestParam(defaultValue = "50") int limit) {
        Pageable page = PageRequest.of(0, clamp(limit));
        return sensorId == null
                ? decisions.findAllByOrderByDecidedAtDesc(page)
                : decisions.findBySensorIdOrderByDecidedAtDesc(sensorId, page);
    }

    @GetMapping("/readings")
    public List<SensorReading> readings(@RequestParam(required = false) String sensorId,
                                        @RequestParam(defaultValue = "50") int limit) {
        Pageable page = PageRequest.of(0, clamp(limit));
        return sensorId == null
                ? readings.findAllByOrderByRecordedAtDesc(page)
                : readings.findBySensorIdOrderByRecordedAtDesc(sensorId, page);
    }

    private int clamp(int limit) {
        return Math.max(1, Math.min(limit, 500));
    }
}