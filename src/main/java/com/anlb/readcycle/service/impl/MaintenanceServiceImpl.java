package com.anlb.readcycle.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.SystemConfig;
import com.anlb.readcycle.repository.SystemConfigRepository;
import com.anlb.readcycle.service.IMaintenanceService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final SystemConfigRepository systemConfigRepository;

    @Override
    public SystemConfig getMaintenance() {
        return systemConfigRepository.findById(1L).orElseGet(() -> {
            SystemConfig maintenance = SystemConfig
                    .builder()
                    .maintenanceMode(false)
                    .build();
            return systemConfigRepository.save(maintenance);
        });
    }

    @Override
    public SystemConfig toggleMaintenanceMode(boolean status) {
        SystemConfig maintenance = getMaintenance();
        maintenance.setMaintenanceMode(status);
        return maintenance;
    }

}
