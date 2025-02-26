package com.anlb.readcycle.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.IActivityLogService;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ActivityLogAdminController {

    private final IActivityLogService activityLogService;

    /**
     * {@code GET  /activity-log} : get all activity logs with optional filtering and pagination.
     *
     * @param spec     The {@link Specification} used to filter the activity logs.
     * @param pageable The {@link Pageable} object containing pagination details.
     * @return A {@link ResponseEntity} containing a {@link ResultPaginateDto} with the list of activity logs and pagination metadata.
     */
    @GetMapping("/activity-log")
    @ApiMessage("Get all activity logs")
    public ResponseEntity<ResultPaginateDto> getAllActivityLog(Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(activityLogService.handleGetAllActivityLog(pageable));
    }
}
