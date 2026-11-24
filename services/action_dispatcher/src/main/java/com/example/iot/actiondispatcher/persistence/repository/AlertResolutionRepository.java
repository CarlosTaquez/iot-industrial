package com.example.iot.actiondispatcher.persistence.repository;

import com.example.iot.actiondispatcher.persistence.entity.AlertResolution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertResolutionRepository extends JpaRepository<AlertResolution, String> { }
