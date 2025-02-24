package com.anlb.readcycle.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    /**
     * Logs an activity for the given user.
     *
     * @param user         The {@link User} who performed the activity.
     * @param activityLog  The {@link ActivityLog} object containing activity details.
     */
    public void log(User user, ActivityLog activityLog) {
        activityLog.setUsername(user.getEmail());
        activityLogRepository.save(activityLog);
    }

    /**
     * Retrieves all activity logs.
     *
     * @param spec     The {@link Specification} used to filter the activity logs.
     * @param pageable The {@link Pageable} object containing pagination details.
     * @return A {@link ResultPaginateDto} containing the list of activity logs and pagination metadata.
     */
    public ResultPaginateDto handleGetAllActivityLog(Specification<ActivityLog> spec, Pageable pageable) {
        Page<ActivityLog> pageActivityLog = activityLogRepository.findAll(spec, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageActivityLog.getTotalPages());
        meta.setTotal(pageActivityLog.getTotalElements());

        response.setMeta(meta);

        List<ActivityLog> listActivityLog = pageActivityLog.getContent();
        response.setResult(listActivityLog);
        return response;
    }
}
