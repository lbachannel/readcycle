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

    /**
     * Retrieves the current system maintenance configuration.
     *
     * If no configuration is found, a new {@link SystemConfig} instance with
     * maintenance mode disabled is created and saved to the repository.
     *
     * @return the {@link SystemConfig} object representing the current maintenance status.
     */
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

    /**
     * Updates the system's maintenance mode status.
     *
     * This method retrieves the current system configuration and updates 
     * the maintenance mode state based on the provided value.
     *
     * @param maintenanceMode the new maintenance mode state (true for enabled, false for disabled).
     * @return the updated {@link SystemConfig} object after saving the changes.
     */
    @Override
    public SystemConfig toggleMaintenanceMode(boolean maintenanceMode) {
        SystemConfig maintenance = getMaintenance();
        maintenance.setMaintenanceMode(maintenanceMode);
        return systemConfigRepository.save(maintenance);
    }

}
