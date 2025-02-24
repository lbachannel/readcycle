package com.anlb.readcycle.mapper;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.dto.response.CreateRoleResponseDto;
import com.anlb.readcycle.dto.response.UpdateRoleResponseDto;

@Service
public class RoleMapper {
    
    /**
     * Converts a {@link Role} entity to a {@link CreateRoleResponseDto}.
     *
     * @param role The {@link Role} entity to be converted.
     * @return A {@link CreateRoleResponseDto} containing role details.
     */
    public CreateRoleResponseDto convertRoleToCreateRoleResponseDTO(Role role) {
        CreateRoleResponseDto response = new CreateRoleResponseDto();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setActive(role.isActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setCreatedBy(role.getCreatedBy());
        response.setPermissions(role.getPermissions());
        return response;
    }

    /**
     * Converts a {@link Role} entity to an {@link UpdateRoleResponseDto}.
     *
     * @param role The {@link Role} entity to be converted.
     * @return An {@link UpdateRoleResponseDto} containing updated role details.
     */
    public UpdateRoleResponseDto convertRoleToUpdateRoleResponseDTO(Role role) {
        UpdateRoleResponseDto response = new UpdateRoleResponseDto();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setActive(role.isActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setCreatedBy(role.getCreatedBy());
        response.setUpdatedAt(role.getUpdatedAt());
        response.setUpdatedBy(role.getUpdatedBy());
        response.setPermissions(role.getPermissions());
        return response;
    }
}
