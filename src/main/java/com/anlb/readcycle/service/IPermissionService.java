package com.anlb.readcycle.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.dto.request.CreatePermissionRequestDto;
import com.anlb.readcycle.dto.request.UpdatePermissionRequestDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IPermissionService {
    void permissionExists(String module, String apiPath, String method) throws InvalidException;
    Permission handleCreatePermission(CreatePermissionRequestDto permissionDTO);
    Permission handleFindById(long id) throws InvalidException;
    Permission handleUpdatePermission(UpdatePermissionRequestDto permissionDTO) throws InvalidException;
    ResultPaginateDto handleGetPermissions(Specification<Permission> spec, Pageable pageable);
    void delete(long id) throws InvalidException;
}
