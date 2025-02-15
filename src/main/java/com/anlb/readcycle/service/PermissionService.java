package com.anlb.readcycle.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.dto.request.UpdatePermissionRequestDTO;
import com.anlb.readcycle.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UpdatePermissionResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO.Meta;
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

    /**
     * Creates a new permission based on the provided request data and saves it to the repository.
     *
     * @param permissionDTO The request data containing permission details such as name, API path, method, and module.
     * @return The newly created {@link Permission} entity after being saved to the repository.
     */
    public Permission handleCreatePermission(CreatePermissionRequestDTO permissionDTO) {
        Permission newPermission = new Permission();
        newPermission.setName(permissionDTO.getName());
        newPermission.setApiPath(permissionDTO.getApiPath());
        newPermission.setMethod(permissionDTO.getMethod());
        newPermission.setModule(permissionDTO.getModule());
        return this.permissionRepository.save(newPermission);
    }

    /**
     * Retrieves a permission by its ID.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param id The ID of the permission to be retrieved.
     * @return The {@link Permission} entity if found.
     * @throws InvalidException if no permission with the given ID exists.
     */
    public Permission handleFindById(long id) throws InvalidException {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new InvalidException("Permission with id: " + id + " does not exist.");
        }
        return this.permissionRepository.findById(id).get();
    }

    /**
     * Updates an existing {@link Permission} entity based on the provided request data.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param permissionDTO The {@link UpdatePermissionRequestDTO} containing updated permission details.
     * @return The updated {@link Permission} entity after being saved to the repository.
     * @throws InvalidException if the permission with the given ID does not exist.
     */
    public Permission handleUpdatePermission(UpdatePermissionRequestDTO permissionDTO) throws InvalidException {
        Permission updatePermission = this.handleFindById(permissionDTO.getId());
        updatePermission.setName(permissionDTO.getName());
        updatePermission.setApiPath(permissionDTO.getApiPath());
        updatePermission.setMethod(permissionDTO.getMethod());
        updatePermission.setModule(permissionDTO.getModule());
        return this.permissionRepository.save(updatePermission);
    }

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

    /**
     * Retrieves a paginated list of {@link Permission} entities based on the given specifications.
     *
     * @param spec The {@link Specification} used to filter permissions.
     * @param pageable The {@link Pageable} object defining pagination and sorting criteria.
     * @return A {@link ResultPaginateDTO} containing the paginated list of permissions along with metadata.
     */
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

    /**
     * Deletes a {@link Permission} entity by its ID.
     * Before deletion, it removes the permission from all associated roles.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param id The ID of the permission to be deleted.
     * @throws InvalidException if the permission with the given ID does not exist.
     */
    public void delete(long id) throws InvalidException {
        // delete permission_role
        Permission currentPermission = this.handleFindById(id);
        currentPermission
                .getRoles()
                .forEach(role -> role.getPermissions().remove(currentPermission));
        // delete permission
        this.permissionRepository.delete(currentPermission);
    }
}
