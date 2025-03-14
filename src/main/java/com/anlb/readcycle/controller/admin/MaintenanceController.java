package com.anlb.readcycle.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.SystemConfig;
import com.anlb.readcycle.dto.request.MaintenanceModeRequestDto;
import com.anlb.readcycle.service.IMaintenanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    /**
     * {@code GET  /maintenance} : Retrieves the current maintenance status of the system.
     *
     * This endpoint returns the system's maintenance configuration,
     * which may indicate whether the system is in maintenance mode.
     *
     * @return a {@link ResponseEntity} containing the {@link SystemConfig} object
     *         with the current maintenance status.
     */
    @GetMapping("/maintenance")
    public ResponseEntity<SystemConfig> getMaintenanceStatus() {
        SystemConfig maintenance = maintenanceService.getMaintenance();
        return ResponseEntity.ok(maintenance);
    }

    /**
     * {@code PUT  /toggle-maintenance} : Toggles the maintenance mode of the system.
     *
     * This endpoint allows updating the system's maintenance status based on the provided request body.
     *
     * @param maintenanceReqDto a {@link MaintenanceModeRequestDto} containing the desired maintenance mode state.
     * @return a {@link ResponseEntity} containing the updated {@link SystemConfig} object if successful,
     *         or an {@link HttpStatus#INTERNAL_SERVER_ERROR} response if an error occurs.
     */
    @PutMapping("/toggle-maintenance")
    public ResponseEntity<SystemConfig> toggleMaintenance(@RequestBody MaintenanceModeRequestDto maintenanceReqDto) {
        System.out.println("check status: " + maintenanceReqDto.isMaintenanceMode());
        try {
            SystemConfig maintenance = maintenanceService.toggleMaintenanceMode(maintenanceReqDto.isMaintenanceMode());
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
