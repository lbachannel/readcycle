package com.anlb.readcycle.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.Permission;
import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.dto.request.CreateRoleRequestDto;
import com.anlb.readcycle.dto.request.UpdateRoleRequestDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto.Meta;
import com.anlb.readcycle.repository.PermissionRepository;
import com.anlb.readcycle.repository.RoleRepository;
import com.anlb.readcycle.service.IRoleService;
import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Checks if a role with the given name already exists.
     * If the role exists, an {@link InvalidException} is thrown.
     *
     * @param name The name of the role to check.
     * @throws InvalidException if a role with the given name already exists.
     */
    @Override
    public void existByName(String name) throws InvalidException {
        boolean roleExits = roleRepository.existsByName(name);
        if (roleExits) {
            throw new InvalidException("Role with name: " + name + " already exists");
        }
    }

    /**
     * Checks if a role exists by its ID.
     *
     * @param id The ID of the role to check.
     * @throws InvalidException if the role does not exist.
     */
    @Override
    public void checkRoleExitsById(long id) throws InvalidException {
        if (!roleRepository.existsById(id)) {
            throw new InvalidException("Role with id: " + id + " does not exist.");
        }
    }

    /**
     * Creates a new role based on the provided role data.
     * If permissions are provided, they are retrieved from the database and assigned to the new role.
     *
     * @param roleDto The {@link CreateRoleRequestDto} containing role details.
     * @return The newly created {@link Role} entity.
     */
    @Override
    public Role handleCreateRole(CreateRoleRequestDto roleDto) {
        Role newRole = new Role();
        newRole.setName(roleDto.getName());
        newRole.setDescription(roleDto.getDescription());
        newRole.setActive(roleDto.isActive());
        if (roleDto.getPermissions() != null) {
            List<Long> reqPermissions = roleDto.getPermissions()
                                            .stream()
                                            .map(x -> x.getId())
                                            .collect(Collectors.toList());
            List<Permission> dbPermissions = permissionRepository.findByIdIn(reqPermissions);

            newRole.setPermissions(dbPermissions);
        }
        return roleRepository.save(newRole);
    }

    /**
     * Retrieves a {@link Role} entity by its ID.
     *
     * @param id The ID of the role to retrieve.
     * @return An {@link Optional} containing the {@link Role} if found, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<Role> handleFindById(long id) {
        return roleRepository.findById(id);
    }

    /**
     * Finds a {@link Role} entity by its name.
     *
     * @param name the name of the role to find
     * @return the {@link Role} entity if found
     * @throws InvalidException if no role with the given name exists
     */
    @Override
    public Role handleFindByName(String name) throws InvalidException {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            throw new InvalidException("Role with name: " + name + " does exists");
        }
        return role;
    }

    /**
     * Updates an existing {@link Role} entity with the provided details.
     *
     * @param roleDTO The {@link UpdateRoleRequestDto} containing updated role information.
     * @return The updated {@link Role} entity.
     * @throws NoSuchElementException if the role with the given ID does not exist.
     */
    @Override
    public Role handleUpdateRole(UpdateRoleRequestDto roleDto) {
        Role updateRole = roleRepository.findById(roleDto.getId()).get();
        updateRole.setName(roleDto.getName());
        updateRole.setDescription(roleDto.getDescription());
        updateRole.setActive(roleDto.isActive());
        if (roleDto.getPermissions() != null) {
            List<Long> reqPermissions = roleDto.getPermissions()
                                            .stream()
                                            .map(x -> x.getId())
                                            .collect(Collectors.toList());
            List<Permission> dbPermissions = permissionRepository.findByIdIn(reqPermissions);

            updateRole.setPermissions(dbPermissions);
        }
        return roleRepository.save(updateRole);
    }

    /**
     * Retrieves a paginated list of roles based on the given specification and pagination details.
     *
     * @param spec     The {@link Specification} used to filter roles.
     * @param pageable The {@link Pageable} object containing pagination and sorting information.
     * @return A {@link ResultPaginateDto} containing the paginated list of roles and metadata.
     */
    @Override
    public ResultPaginateDto handleGetRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> dbRoles = roleRepository.findAll(spec, pageable);
        ResultPaginateDto resultPaginateDto = new ResultPaginateDto();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(dbRoles.getTotalPages());
        meta.setTotal(dbRoles.getTotalElements());

        resultPaginateDto.setMeta(meta);
        resultPaginateDto.setResult(dbRoles.getContent());

        return resultPaginateDto;
    }

    /**
     * Deletes a role by its unique identifier.
     *
     * @param id The ID of the role to be deleted.
     */
    @Override
    public void handleDeleteRoleById(long id) {
        roleRepository.deleteById(id);
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id The ID of the role to be retrieved.
     * @return The {@link Role} object if found.
     * @throws InvalidException if the role with the given ID does not exist.
     */
    @Override
    public Role handleGetRoleById(long id) throws InvalidException {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new InvalidException("Role with id: " + id + " does not exist");
        }
        return role.get();
    }
}
