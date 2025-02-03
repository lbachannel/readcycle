package com.anlb.readcycle.service;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(CreatePermissionRequestDTO permissionDTO) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
            permissionDTO.getModule(),
            permissionDTO.getApiPath(),
            permissionDTO.getMethod()
        );
    }

    public Permission handleCreatePermission(CreatePermissionRequestDTO permissionDTO) {
        Permission newPermission = new Permission();
        newPermission.setName(permissionDTO.getName());
        newPermission.setApiPath(permissionDTO.getApiPath());
        newPermission.setMethod(permissionDTO.getMethod());
        newPermission.setModule(permissionDTO.getModule());
        return this.permissionRepository.save(newPermission);
    }

    // convert permission -> create permission response dto
    public CreatePermissionResponseDTO convertPermissionToCreatePermissionResponseDTO(Permission permission) {
        CreatePermissionResponseDTO response = new CreatePermissionResponseDTO();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setApiPath(permission.getApiPath());
        response.setMethod(permission.getMethod());
        response.setModule(permission.getModule());
        response.setCreatedAt(permission.getCreatedAt());
        response.setCreatedBy(permission.getCreatedBy());
        return response;
    }
}
