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
            log.error("logging activity error: {}", e);
        }
    }

    /**
     * Logs the update activity of a user.
     *
     * <p>This method compares the old and new user data to identify changes. If any relevant 
     * attributes (such as date of birth, name, or role) have been updated, an {@link ActivityLog} 
     * entry is created and logged.
     *
     * @param oldUser   the user's data before the update
     * @param newUser   the user's data after the update
     * @param userLogin the user performing the update action
     */
    public void logUpdateUser(User oldUser, User newUser, User userLogin) {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("userId", String.valueOf(newUser.getId()), "User id"));
            
            if (!StringUtils.equals(String.valueOf(oldUser.getDateOfBirth()), String.valueOf(newUser.getDateOfBirth()))) {
                descriptions.add(ActivityDescription.from("dateOfBirth", oldUser.getDateOfBirth() + " → " + newUser.getDateOfBirth(), "Date of birth"));
            }
            
            if (!StringUtils.equals(oldUser.getName(), newUser.getName())) {
                descriptions.add(ActivityDescription.from("name", oldUser.getName() + " → " + newUser.getName(), "Name"));
            }

            if (!StringUtils.equals(oldUser.getRole().getName(), newUser.getRole().getName())) {
                descriptions.add(ActivityDescription.from("role", oldUser.getRole().getName() + " → " + newUser.getRole().getName(), "Role"));
            }

            if (descriptions.size() > 1) {
                ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.USER, ActivityType.UPDATE_USER, descriptions);
                activityLogService.log(userLogin, activityLog);
            }
        } catch (Exception e) {
            log.error("logging activity error: {}", e);
        }
    }

    /**
     * Logs the deletion of a user.
     *
     * @param id the ID of the deleted user
     * @param userLogin the user performing the deletion
     */
    public void logDeleteUser(long id, User userLogin) {
        try {
            List<ActivityDescription> descriptions = new ArrayList<>();
            descriptions.add(ActivityDescription.from("userId", String.valueOf(id) + " → " + "none" , "User id"));
            ActivityLog activityLog = ActivityLog.formatLogMessage(ActivityGroup.USER, ActivityType.DELETE_USER, descriptions);
            activityLogService.log(userLogin, activityLog);
        } catch (Exception e) {
            log.error("logging activity error: {}", e);
        }
    }
}
