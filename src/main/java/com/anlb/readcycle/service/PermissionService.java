package com.anlb.readcycle.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO.Meta;
import com.anlb.readcycle.domain.dto.response.UpdatePermissionResponseDTO;
import com.anlb.readcycle.repository.PermissionRepository;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * Checks whether a permission with the given module, API path, and method already exists.
     * If the permission exists, an {@link InvalidException} is thrown.
     *
     * @param module  The module associated with the permission.
     * @param apiPath The API path of the permission.
     * @param method  The HTTP method of the permission.
     * @throws InvalidException if a permission with the same module, API path, and method already exists.
     */
    public void permissionExists(String module, String apiPath, String method) throws InvalidException {
        boolean checkPermissionExists = permissionRepository.existsByModuleAndApiPathAndMethod(
            module,
            apiPath,
            method
        );

        if (checkPermissionExists) {
            throw new InvalidException("Permission already exists");
        }
    }

    public Permission handleCreatePermission(CreatePermissionRequestDTO permissionDTO) {
        Permission newPermission = new Permission();
        newPermission.setName(permissionDTO.getName());
        newPermission.setApiPath(permissionDTO.getApiPath());
        newPermission.setMethod(permissionDTO.getMethod());
        newPermission.setModule(permissionDTO.getModule());
        return this.permissionRepository.save(newPermission);
    }

    public Permission handleFindById(long id) throws InvalidException {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new InvalidException("Permission with id: " + id + " does not exist.");
        }
        return this.permissionRepository.findById(id).get();
    }

    public Permission handleUpdatePermission(UpdatePermissionRequestDTO permissionDTO) {
        Permission updatePermission = this.permissionRepository.findById(permissionDTO.getId()).get();
        updatePermission.setName(permissionDTO.getName());
        updatePermission.setApiPath(permissionDTO.getApiPath());
        updatePermission.setMethod(permissionDTO.getMethod());
        updatePermission.setModule(permissionDTO.getModule());
        return this.permissionRepository.save(updatePermission);
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

    public ResultPaginateDTO handleGetPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> dbPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginateDTO resultPaginateDTO = new ResultPaginateDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(dbPermissions.getTotalPages());
        meta.setTotal(dbPermissions.getTotalElements());

        resultPaginateDTO.setMeta(meta);
        resultPaginateDTO.setResult(dbPermissions.getContent());

        return resultPaginateDTO;
    }

    public void delete(long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission
                .getRoles()
                .forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }
}
