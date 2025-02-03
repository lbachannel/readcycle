package com.anlb.readcycle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.domain.dto.request.CreateRoleRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdateRoleRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateRoleResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO.Meta;
import com.anlb.readcycle.domain.dto.response.UpdateRoleResponseDTO;
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

    public UpdateRoleResponseDTO convertRoleToUpdateRoleResponseDTO(Role role) {
        UpdateRoleResponseDTO response = new UpdateRoleResponseDTO();
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

    public Optional<Role> handleFindById(long id) {
        return this.roleRepository.findById(id);
    }

    public Role handleUpdateRole(UpdateRoleRequestDTO roleDTO) {
        Role updateRole = this.roleRepository.findById(roleDTO.getId()).get();
        updateRole.setName(roleDTO.getName());
        updateRole.setDescription(roleDTO.getDescription());
        updateRole.setActive(roleDTO.isActive());
        if (roleDTO.getPermissions() != null) {
            List<Long> reqPermissions = roleDTO.getPermissions()
                                            .stream()
                                            .map(x -> x.getId())
                                            .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);

            updateRole.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(updateRole);
    }

    public ResultPaginateDTO handleGetRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> dbRoles = this.roleRepository.findAll(spec, pageable);
        ResultPaginateDTO resultPaginateDTO = new ResultPaginateDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(dbRoles.getTotalPages());
        meta.setTotal(dbRoles.getTotalElements());

        resultPaginateDTO.setMeta(meta);
        resultPaginateDTO.setResult(dbRoles.getContent());

        return resultPaginateDTO;
    }

    public void handleDeleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }
}
