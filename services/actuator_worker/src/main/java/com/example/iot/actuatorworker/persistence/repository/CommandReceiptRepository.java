package com.example.iot.actuatorworker.persistence.repository;

import com.example.iot.actuatorworker.persistence.entity.CommandReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandReceiptRepository extends JpaRepository<CommandReceipt, String> { }
