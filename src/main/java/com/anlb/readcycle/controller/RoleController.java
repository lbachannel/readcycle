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

import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.dto.request.CreateRoleRequestDTO;
import com.anlb.readcycle.dto.request.UpdateRoleRequestDTO;
import com.anlb.readcycle.dto.response.CreateRoleResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UpdateRoleResponseDTO;
import com.anlb.readcycle.service.RoleService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws InvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        return ResponseEntity.ok().body(role);
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<CreateRoleResponseDTO> createRole(@Valid @RequestBody CreateRoleRequestDTO roleDTO) throws InvalidException {
        this.roleService.existByName(roleDTO.getName());
        Role role = this.roleService.handleCreateRole(roleDTO);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.roleService.convertRoleToCreateRoleResponseDTO(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<UpdateRoleResponseDTO> updateRole(@Valid @RequestBody UpdateRoleRequestDTO roleDTO) throws InvalidException {
        // check if role does not exits. then throw exception
        this.roleService.checkRoleExitsById(roleDTO.getId());
        Role updateRole = this.roleService.handleUpdateRole(roleDTO);
        return ResponseEntity
                    .ok()
                    .body(this.roleService.convertRoleToUpdateRoleResponseDTO(updateRole));
    }

    @GetMapping("/roles")
    @ApiMessage("Get roles")
    public ResponseEntity<ResultPaginateDTO> getPermissions(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.handleGetRoles(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws InvalidException {
        // check if role does not exits. then throw exception
        this.roleService.checkRoleExitsById(id);
        this.roleService.handleDeleteRoleById(id);
        return ResponseEntity.ok().body(null);
    }
}
