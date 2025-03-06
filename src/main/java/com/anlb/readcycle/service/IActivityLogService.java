package com.anlb.readcycle.service;

import org.springframework.data.domain.Pageable;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.criteria.ActivityCriteria;

public interface IActivityLogService {
    void log(User user, ActivityLog activityLog);
    ResultPaginateDto handleGetAllActivityLog(ActivityCriteria criteria, Pageable pageable);
}
