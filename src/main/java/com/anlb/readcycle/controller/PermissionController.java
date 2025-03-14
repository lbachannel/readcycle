package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.dto.request.CreatePermissionRequestDto;
import com.anlb.readcycle.dto.request.UpdatePermissionRequestDto;
import com.anlb.readcycle.dto.response.CreatePermissionResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.UpdatePermissionResponseDto;
import com.anlb.readcycle.mapper.PermissionMapper;
import com.anlb.readcycle.service.IPermissionService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;
    private final PermissionMapper permissionMapper;

    /**
     * {@code POST  /permissions} : Creates a new permission.
     *
     * @param permissionDto The request data containing module, API path, and method for the permission.
     * @return A {@link ResponseEntity} containing the created permission details in a {@link CreatePermissionResponseDto}.
     * @throws InvalidException If the permission already exists or validation fails.
     */
    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<CreatePermissionResponseDto> createPermission(@Valid @RequestBody CreatePermissionRequestDto permissionDto) throws InvalidException {
        // check if permission exists
        permissionService.permissionExists(permissionDto.getModule(), permissionDto.getApiPath(), permissionDto.getMethod());
        Permission newPermission = this.permissionService.handleCreatePermission(permissionDto);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(permissionMapper.convertPermissionToCreatePermissionResponseDto(newPermission));
    }

    /**
     * {@code PUT  /permissions} : Updates an existing permission.
     *
     * @param permissionDto The request data containing module, API path, and method for the permission update.
     * @return A {@link ResponseEntity} containing the updated permission details in a {@link UpdatePermissionResponseDto}.
     * @throws InvalidException If the permission does not exist or validation fails.
     */
    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<UpdatePermissionResponseDto> updatePermission(@Valid @RequestBody UpdatePermissionRequestDto permissionDto) throws InvalidException {
        // check if permission exists
        permissionService.permissionExists(permissionDto.getModule(), permissionDto.getApiPath(), permissionDto.getMethod());
        Permission updatePermission = permissionService.handleUpdatePermission(permissionDto);
        return ResponseEntity
                    .ok()
                    .body(permissionMapper.convertPermissionToUpdatePermissionResponseDto(updatePermission));
    }

    /**
     * {@code GET  /permissions} : Retrieves a paginated list of permissions
     *                             based on the provided filter criteria.
     *
     * @param spec The filter specification for querying permissions.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of permissions in a {@link ResultPaginateDto}.
     */
    @GetMapping("/permissions")
    @ApiMessage("Get permissions")
    public ResponseEntity<ResultPaginateDto> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(permissionService.handleGetPermissions(spec, pageable));
    }

    /**
     * {@code DELETE  /permissions/{id}} : Deletes a permission by its ID.
     *
     * @param id The ID of the permission to be deleted.
     * @return A {@link ResponseEntity} with an empty body indicating a successful deletion.
     * @throws InvalidException If the permission does not exist or deletion fails.
     */
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws InvalidException {
        permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
