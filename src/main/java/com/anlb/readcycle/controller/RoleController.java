package com.anlb.readcycle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.domain.dto.request.CreateRoleRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateRoleResponseDTO;
import com.anlb.readcycle.service.RoleService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<CreateRoleResponseDTO> createRole(@Valid @RequestBody CreateRoleRequestDTO roleDTO) throws InvalidException {
        if (this.roleService.existByName(roleDTO.getName())) {
            throw new InvalidException("Role with name: " + roleDTO.getName() + " is already exist");
        }

        Role role = this.roleService.handleCreateRole(roleDTO);

        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(this.roleService.convertRoleToCreateRoleResponseDTO(role));
    }
}
