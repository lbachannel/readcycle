package com.anlb.readcycle.dto.activitylog;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.anlb.readcycle.utils.JSON;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activitylogs")
public class ActivityLog {
    @Id
    private String id;

    private String activityType;

    private String activityGroup;

    private Instant executionTime;

    private String description;

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
