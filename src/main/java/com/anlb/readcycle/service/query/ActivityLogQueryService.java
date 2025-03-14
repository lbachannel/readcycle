package com.anlb.readcycle.service.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.dto.activitylog.ActivityLog;
import com.anlb.readcycle.service.criteria.ActivityCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tech.jhipster.service.QueryService;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityLogQueryService extends QueryService<ActivityLog> {
    
    private final MongoTemplate mongoTemplate;

    /**
     * Retrieves a paginated list of activity logs that match the specified criteria.
     *
     * This method constructs a MongoDB {@link Query} based on the provided {@link ActivityCriteria} 
     * and fetches matching activity logs with pagination.
     *
     * @param activityCriteria the filtering criteria containing conditions for querying activity logs.
     * @param pageable the pagination information including page number and size.
     * @return a {@link Page} containing activity logs that match the specified criteria.
     */
    public Page<ActivityLog> findByCriteria(ActivityCriteria activityCriteria, Pageable pageable) {
        log.debug("find by criteria : {}, page: {}", activityCriteria, pageable);

        Query query = new Query();

        if (activityCriteria.getActivityGroup() != null && activityCriteria.getActivityGroup().getEquals() != null) {
            query.addCriteria(Criteria.where("activityGroup").is(activityCriteria.getActivityGroup().getEquals()));
        }

        if (activityCriteria.getActivityType() != null) {
            if (activityCriteria.getActivityType().getEquals() != null) {
                query.addCriteria(Criteria.where("activityType").is(activityCriteria.getActivityType().getEquals()));
            } 
            if (activityCriteria.getActivityType().getIn() != null && !activityCriteria.getActivityType().getIn().isEmpty()) {
                query.addCriteria(Criteria.where("activityType").in(activityCriteria.getActivityType().getIn()));
            }
        }

        long total = mongoTemplate.count(query, ActivityLog.class);
        List<ActivityLog> logs = mongoTemplate.find(query.with(pageable), ActivityLog.class);

        return new PageImpl<>(logs, pageable, total);
    }

}
