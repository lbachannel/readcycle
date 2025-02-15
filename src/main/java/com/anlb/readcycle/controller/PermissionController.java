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
import com.anlb.readcycle.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.dto.request.UpdatePermissionRequestDTO;
import com.anlb.readcycle.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UpdatePermissionResponseDTO;
import com.anlb.readcycle.service.PermissionService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<CreatePermissionResponseDTO> createPermission(@Valid @RequestBody CreatePermissionRequestDTO permissionDTO) throws InvalidException {
        // check if permission exists
        this.permissionService.permissionExists(permissionDTO.getModule(), permissionDTO.getApiPath(), permissionDTO.getMethod());
        Permission newPermission = this.permissionService.handleCreatePermission(permissionDTO);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.permissionService.convertPermissionToCreatePermissionResponseDTO(newPermission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<UpdatePermissionResponseDTO> updatePermission(@Valid @RequestBody UpdatePermissionRequestDTO permissionDTO) throws InvalidException {
        // check if permission exists
        this.permissionService.permissionExists(permissionDTO.getModule(), permissionDTO.getApiPath(), permissionDTO.getMethod());
        Permission updatePermission = this.permissionService.handleUpdatePermission(permissionDTO);
        return ResponseEntity
                    .ok()
                    .body(this.permissionService.convertPermissionToUpdatePermissionResponseDTO(updatePermission));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get permissions")
    public ResponseEntity<ResultPaginateDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.handleGetPermissions(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws InvalidException {
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
