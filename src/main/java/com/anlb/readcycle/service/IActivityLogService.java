package com.anlb.readcycle.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.response.ResultPaginateDto;

public interface IActivityLogService {
    void log(User user, ActivityLog activityLog);
    ResultPaginateDto handleGetAllActivityLog(Specification<ActivityLog> spec, Pageable pageable);
}
