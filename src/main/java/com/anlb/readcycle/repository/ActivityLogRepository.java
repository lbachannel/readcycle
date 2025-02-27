package com.anlb.readcycle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.dto.activitylog.ActivityLog;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {}
