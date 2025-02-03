package com.anlb.readcycle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.domain.dto.request.CreateRoleRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateRoleResponseDTO;
import com.anlb.readcycle.repository.PermissionRepository;
import com.anlb.readcycle.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
                        PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(CreateRoleRequestDTO roleDTO) {
        Role newRole = new Role();
        newRole.setName(roleDTO.getName());
        newRole.setDescription(roleDTO.getDescription());
        newRole.setActive(roleDTO.isActive());
        if (roleDTO.getPermissions() != null) {
            List<Long> reqPermissions = roleDTO.getPermissions()
                                            .stream()
                                            .map(x -> x.getId())
                                            .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);

            newRole.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(newRole);
    }

    public CreateRoleResponseDTO convertRoleToCreateRoleResponseDTO(Role role) {
        CreateRoleResponseDTO response = new CreateRoleResponseDTO();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setActive(role.isActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setCreatedBy(role.getCreatedBy());
        response.setPermissions(role.getPermissions());
        return response;
    }
}
