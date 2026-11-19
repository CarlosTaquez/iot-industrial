package com.example.iot.actuatorworker;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandReceiptRepository extends JpaRepository<CommandReceipt, String> { }
