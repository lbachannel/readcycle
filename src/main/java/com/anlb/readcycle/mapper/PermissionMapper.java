package com.anlb.readcycle.mapper;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.dto.response.UpdatePermissionResponseDTO;

@Service
public class PermissionMapper {
    
    /**
     * Converts a {@link Permission} entity into a {@link CreatePermissionResponseDTO}.
     *
     * @param permission The {@link Permission} entity to be converted.
     * @return A {@link CreatePermissionResponseDTO} containing the permission details.
     */
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

    /**
     * Converts a {@link Permission} entity into an {@link UpdatePermissionResponseDTO}.
     *
     * @param permission The {@link Permission} entity to be converted.
     * @return An {@link UpdatePermissionResponseDTO} containing the permission details, including update information.
     */
    public UpdatePermissionResponseDTO convertPermissionToUpdatePermissionResponseDTO(Permission permission) {
        UpdatePermissionResponseDTO response = new UpdatePermissionResponseDTO();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setApiPath(permission.getApiPath());
        response.setMethod(permission.getMethod());
        response.setModule(permission.getModule());
        response.setCreatedAt(permission.getCreatedAt());
        response.setCreatedBy(permission.getCreatedBy());
        response.setUpdatedAt(permission.getUpdatedAt());
        response.setUpdatedBy(permission.getUpdatedBy());
        return response;
    }
}
