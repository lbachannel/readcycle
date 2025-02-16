package com.anlb.readcycle.service;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public void log(User user, ActivityLog activityLog) {
        activityLog.setUsername(user.getEmail());
        this.activityLogRepository.save(activityLog);
    }
}
