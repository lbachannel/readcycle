package com.anlb.readcycle.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.repository.ActivityLogRepository;
import com.anlb.readcycle.service.IActivityLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements IActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    /**
     * Logs an activity for the given user.
     *
     * @param user         The {@link User} who performed the activity.
     * @param activityLog  The {@link ActivityLog} object containing activity details.
     */
    @Override
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
    @Override
    public ResultPaginateDto handleGetAllActivityLog(Pageable pageable) {
        Page<ActivityLog> pageActivityLog = activityLogRepository.findAll(pageable);
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
