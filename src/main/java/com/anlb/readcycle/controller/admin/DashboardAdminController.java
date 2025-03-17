package com.anlb.readcycle.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.response.AdminDashboardResponseDto;
import com.anlb.readcycle.service.IDashboardService;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DashboardAdminController {

    private final IDashboardService dashboardService;
    
    @GetMapping("/dashboard")
    @ApiMessage("Count users & books")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
    
    @GetMapping("/dashboard-books")
    @ApiMessage("Stats books")
    public ResponseEntity<AdminDashboardResponseDto> getDashboardStatsBooks() {
        return ResponseEntity.ok(dashboardService.getDashboardStatsBooks());
    }
}
