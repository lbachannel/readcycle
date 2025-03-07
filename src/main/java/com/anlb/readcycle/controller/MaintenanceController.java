package com.anlb.readcycle.controller;

import java.time.Instant;

import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.response.MaintenanceDto;
import com.anlb.readcycle.utils.anotation.ApiMessage;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private ApplicationAvailability availability;
    private ApplicationEventPublisher eventPublisher;

    public MaintenanceController(ApplicationAvailability applicationAvailability, ApplicationEventPublisher eventPublisher) {
        this.availability = applicationAvailability;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    @ApiMessage("Get maintenance status")
    public ResponseEntity<MaintenanceDto> retreiveInMaintenance() {
        var lastChangeEvent = availability.getLastChangeEvent(ReadinessState.class);
        return ResponseEntity.ok(
            new MaintenanceDto(
                lastChangeEvent.getState().equals(ReadinessState.REFUSING_TRAFFIC), 
                Instant.ofEpochMilli(lastChangeEvent.getTimestamp())
            )
        );
    }

    @PutMapping
    public ResponseEntity<Void> initInMaintenance(@RequestBody String inMaintenance) {
        AvailabilityChangeEvent.publish(eventPublisher, this, Boolean.valueOf(inMaintenance) ? ReadinessState.REFUSING_TRAFFIC : ReadinessState.ACCEPTING_TRAFFIC);
        return ResponseEntity.noContent().build();
    }
}
