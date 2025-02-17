package com.anlb.readcycle.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.activitylog.ActivityDescription;
import com.anlb.readcycle.dto.activitylog.ActivityGroup;
import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.dto.activitylog.ActivityType;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogService {

    private final ActivityLogService activityLogService;

    /**
     * Logs the creation of a new user.
     *
     * @param user       the newly created user
     * @param userLogin  the user performing the creation action
     * @throws InvalidException if any issue occurs during logging
     */
    public void logCreateUser(User user, User userLogin) throws InvalidException {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("userId", String.valueOf(user.getId()), "User id"));

            if (!StringUtils.isBlank(user.getName())) {
                descriptions.add(ActivityDescription.from("name", user.getName(), "Name"));
            }

            if (!StringUtils.isBlank(user.getEmail())) {
                descriptions.add(ActivityDescription.from("email", user.getEmail(), "Email"));
            }

            descriptions.add(ActivityDescription.from("dateOfBirth", String.valueOf(user.getDateOfBirth()), "Date of birth"));
            
            if (!StringUtils.isBlank(user.getRole().getName())) {
                descriptions.add(ActivityDescription.from("role", user.getRole().getName(), "Role"));
            }

            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.USER, ActivityType.CREATE_USER, descriptions);
            activityLogService.log(userLogin, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: ", e);
        }
    }
}
