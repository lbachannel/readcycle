package com.anlb.readcycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
}
