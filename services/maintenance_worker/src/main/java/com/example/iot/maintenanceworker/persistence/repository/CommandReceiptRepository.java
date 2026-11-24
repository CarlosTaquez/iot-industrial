package com.example.iot.maintenanceworker.persistence.repository;

import com.example.iot.maintenanceworker.persistence.entity.CommandReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandReceiptRepository extends JpaRepository<CommandReceipt, String> { }
