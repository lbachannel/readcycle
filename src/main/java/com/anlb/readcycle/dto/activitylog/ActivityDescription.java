package com.anlb.readcycle.dto.activitylog;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActivityDescription {
    private String key;
    private String value;
    private String label;

    public static ActivityDescription from(String key, String value, String label) {
        return ActivityDescription.builder()
                        .key(key)
                        .value(value)
                        .label(label)
                        .build();
    }
}
