package com.anlb.readcycle.service;

import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.SystemConfig;

@Repository
public interface IMaintenanceService {
    SystemConfig getMaintenance();
    SystemConfig toggleMaintenanceMode(boolean status);
}
