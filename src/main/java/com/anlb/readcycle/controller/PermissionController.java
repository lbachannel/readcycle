package com.anlb.readcycle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.dto.request.CreatePermissionRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreatePermissionResponseDTO;
import com.anlb.readcycle.service.PermissionService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

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
}
