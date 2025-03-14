package com.anlb.readcycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.SystemConfig;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {}
