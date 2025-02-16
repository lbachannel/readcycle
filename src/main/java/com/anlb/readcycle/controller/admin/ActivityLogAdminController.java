package com.anlb.readcycle.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.service.ActivityLogService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ActivityLogAdminController {

    private final ActivityLogService activityLogService;

    /**
     * {@code GET  /activity-log} : get all activity logs with optional filtering and pagination.
     *
     * @param spec     The {@link Specification} used to filter the activity logs.
     * @param pageable The {@link Pageable} object containing pagination details.
     * @return A {@link ResponseEntity} containing a {@link ResultPaginateDTO} with the list of activity logs and pagination metadata.
     */
    @GetMapping("/activity-log")
    @ApiMessage("Get all activity logs")
    public ResponseEntity<ResultPaginateDTO> getAllActivityLog(@Filter Specification<ActivityLog> spec, Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.activityLogService.handleGetAllActivityLog(spec, pageable));
    }
}
