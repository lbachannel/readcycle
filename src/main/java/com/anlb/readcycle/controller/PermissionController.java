package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.request.UpdatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.domain.dto.response.UpdatePermissionResponseDTO;
import com.anlb.readcycle.service.PermissionService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<CreatePermissionResponseDTO> createPermission(@Valid @RequestBody CreatePermissionRequestDTO permissionDTO) throws InvalidException {
        if (this.permissionService.isPermissionExist(permissionDTO)) {
            throw new InvalidException("Permission is already exists");
        }
        Permission newPermission = this.permissionService.handleCreatePermission(permissionDTO);

        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.permissionService.convertPermissionToCreatePermissionResponseDTO(newPermission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<UpdatePermissionResponseDTO> updatePermission(@Valid @RequestBody UpdatePermissionRequestDTO permissionDTO) throws InvalidException {
        if (this.permissionService.handleFindById(permissionDTO.getId()) == null) {
            throw new InvalidException("Permission with id: " + permissionDTO.getId() + " does not exist.");
        }

        if (this.permissionService.isPermissionExist(permissionDTO)) {
            throw new InvalidException("Permission is already exist.");
        }

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
}
