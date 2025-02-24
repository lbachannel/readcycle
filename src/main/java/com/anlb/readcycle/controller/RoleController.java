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
import com.anlb.readcycle.dto.request.CreateRoleRequestDto;
import com.anlb.readcycle.dto.request.UpdateRoleRequestDto;
import com.anlb.readcycle.dto.response.CreateRoleResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UpdateRoleResponseDTO;
import com.anlb.readcycle.mapper.RoleMapper;
import com.anlb.readcycle.service.RoleService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    /**
     * {@code GET  /roles/{id}} : Retrieves a role by its ID.
     *
     * @param id The ID of the role to retrieve.
     * @return A {@link ResponseEntity} containing the requested {@link Role}.
     * @throws InvalidException If the role does not exist.
     */
    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws InvalidException {
        Role role = this.roleService.handleGetRoleById(id);
        return ResponseEntity.ok().body(role);
    }

    /**
     * {@code POST  /roles} : Creates a new role.
     *
     * @param roleDTO The request body containing role details.
     * @return A {@link ResponseEntity} containing the created role as a {@link CreateRoleResponseDTO}.
     * @throws InvalidException If a role with the same name already exists.
     */
    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<CreateRoleResponseDTO> createRole(@Valid @RequestBody CreateRoleRequestDto roleDTO) throws InvalidException {
        this.roleService.existByName(roleDTO.getName());
        Role role = this.roleService.handleCreateRole(roleDTO);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.roleMapper.convertRoleToCreateRoleResponseDTO(role));
    }

    /**
     * {@code PUT  /roles} : Updates an existing role.
     *
     * @param roleDTO The request body containing updated role details.
     * @return A {@link ResponseEntity} containing the updated role as a {@link UpdateRoleResponseDTO}.
     * @throws InvalidException If the role does not exist.
     */
    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<UpdateRoleResponseDTO> updateRole(@Valid @RequestBody UpdateRoleRequestDto roleDTO) throws InvalidException {
        // check if role does not exits. then throw exception
        this.roleService.checkRoleExitsById(roleDTO.getId());
        Role updateRole = this.roleService.handleUpdateRole(roleDTO);
        return ResponseEntity
                    .ok()
                    .body(this.roleMapper.convertRoleToUpdateRoleResponseDTO(updateRole));
    }

    /**
     * {@code GET  /roles} : Retrieves a paginated list of roles
     *                       based on filtering criteria.
     *
     * @param spec     The filtering criteria for retrieving roles.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of roles as a {@link ResultPaginateDTO}.
     */
    @GetMapping("/roles")
    @ApiMessage("Get roles")
    public ResponseEntity<ResultPaginateDTO> getPermissions(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.handleGetRoles(spec, pageable));
    }

    /**
     * {@code DELETE  /roles/{id}} : Deletes a role by its ID.
     *
     * @param id The ID of the role to be deleted.
     * @return A {@link ResponseEntity} with a status of 200 OK if the deletion is successful.
     * @throws InvalidException If the role does not exist.
     */
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws InvalidException {
        // check if role does not exits. then throw exception
        this.roleService.checkRoleExitsById(id);
        this.roleService.handleDeleteRoleById(id);
        return ResponseEntity.ok().body(null);
    }
}
