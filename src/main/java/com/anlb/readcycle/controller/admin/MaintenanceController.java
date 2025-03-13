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

    @GetMapping("/maintenance")
    public ResponseEntity<SystemConfig> getMaintenanceStatus() {
        SystemConfig maintenance = maintenanceService.getMaintenance();
        return ResponseEntity.ok(maintenance);
    }

    @PutMapping("/maintenance")
    public ResponseEntity<SystemConfig> toggleMaintenance(@RequestBody MaintenanceModeRequestDto maintenanceReqDto) {
        try {
            SystemConfig maintenance = maintenanceService.toggleMaintenanceMode(maintenanceReqDto.isStatus());
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
