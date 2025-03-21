package com.anlb.readcycle.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.dto.request.CreatePermissionRequestDto;
import com.anlb.readcycle.dto.request.UpdatePermissionRequestDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto.Meta;
import com.anlb.readcycle.repository.PermissionRepository;
import com.anlb.readcycle.service.IPermissionService;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

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
    @Override
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
     * @param permissionDto The request data containing permission details such as name, API path, method, and module.
     * @return The newly created {@link Permission} entity after being saved to the repository.
     */
    @Override
    public Permission handleCreatePermission(CreatePermissionRequestDto permissionDto) {
        Permission newPermission = new Permission();
        newPermission.setName(permissionDto.getName());
        newPermission.setApiPath(permissionDto.getApiPath());
        newPermission.setMethod(permissionDto.getMethod());
        newPermission.setModule(permissionDto.getModule());
        return permissionRepository.save(newPermission);
    }

    /**
     * Retrieves a permission by its ID.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param id The ID of the permission to be retrieved.
     * @return The {@link Permission} entity if found.
     * @throws InvalidException if no permission with the given ID exists.
     */
    @Override
    public Permission handleFindById(long id) throws InvalidException {
        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new InvalidException("Permission with id: " + id + " does not exist.");
        }
        return permissionRepository.findById(id).get();
    }

    /**
     * Updates an existing {@link Permission} entity based on the provided request data.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param permissionDto The {@link UpdatePermissionRequestDto} containing updated permission details.
     * @return The updated {@link Permission} entity after being saved to the repository.
     * @throws InvalidException if the permission with the given ID does not exist.
     */
    @Override
    public Permission handleUpdatePermission(UpdatePermissionRequestDto permissionDto) throws InvalidException {
        Permission updatePermission = handleFindById(permissionDto.getId());
        updatePermission.setName(permissionDto.getName());
        updatePermission.setApiPath(permissionDto.getApiPath());
        updatePermission.setMethod(permissionDto.getMethod());
        updatePermission.setModule(permissionDto.getModule());
        return permissionRepository.save(updatePermission);
    }

    /**
     * Retrieves a paginated list of {@link Permission} entities based on the given specifications.
     *
     * @param spec The {@link Specification} used to filter permissions.
     * @param pageable The {@link Pageable} object defining pagination and sorting criteria.
     * @return A {@link ResultPaginateDto} containing the paginated list of permissions along with metadata.
     */
    @Override
    public ResultPaginateDto handleGetPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> dbPermissions = permissionRepository.findAll(spec, pageable);
        ResultPaginateDto resultPaginateDto = new ResultPaginateDto();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(dbPermissions.getTotalPages());
        meta.setTotal(dbPermissions.getTotalElements());

        resultPaginateDto.setMeta(meta);
        resultPaginateDto.setResult(dbPermissions.getContent());

        return resultPaginateDto;
    }

    /**
     * Deletes a {@link Permission} entity by its ID.
     * Before deletion, it removes the permission from all associated roles.
     * If the permission does not exist, an {@link InvalidException} is thrown.
     *
     * @param id The ID of the permission to be deleted.
     * @throws InvalidException if the permission with the given ID does not exist.
     */
    @Override
    public void delete(long id) throws InvalidException {
        // delete permission_role
        Permission currentPermission = handleFindById(id);
        currentPermission
                .getRoles()
                .forEach(role -> role.getPermissions().remove(currentPermission));
        // delete permission
        permissionRepository.delete(currentPermission);
    }
}
