package com.example.iot.sensorproducer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulate")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SensorSimulator simulator;

    public SimulationController(SensorSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping
    public ResponseEntity<VibrationReadingRecord> simulateReading() {
        return ResponseEntity.accepted().body(simulator.simulateReading());
    }
}
