package com.anlb.readcycle.service.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.StringFilter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCriteria implements Serializable, Criteria {
    StringFilter activityGroup;
    StringFilter activityType;

    public ActivityCriteria(ActivityCriteria other) {
        this.activityGroup = other.activityGroup == null ? null : other.activityGroup.copy();
        this.activityType = other.activityType == null ? null : other.activityType.copy();
    }

    @Override
    public ActivityCriteria copy() {
        return new ActivityCriteria(this);
    }

}
