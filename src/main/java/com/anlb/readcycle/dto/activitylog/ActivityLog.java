package com.anlb.readcycle.dto.activitylog;

import java.time.Instant;
import java.util.List;

import com.anlb.readcycle.utils.JSON;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "activitylogs")
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "activity_group")
    private String activityGroup;

    @Column(name = "execution_time")
    private Instant executionTime;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "username")
    private String username;

    public static ActivityLog formatLogMessage(ActivityGroup group, ActivityType type, List<ActivityDescription> descriptions) {
        ActivityLog result = ActivityLog.builder()
            .activityGroup(group.toString())
            .activityType(type.toString())
            .executionTime(Instant.now())
            .description(JSON.toJson(descriptions))
            .username("")
            .build();
        return result;
    }
}
