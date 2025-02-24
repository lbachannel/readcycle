package com.anlb.readcycle.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.dto.request.CreateRoleRequestDto;
import com.anlb.readcycle.dto.request.UpdateRoleRequestDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IRoleService {
    void existByName(String name) throws InvalidException;
    void checkRoleExitsById(long id) throws InvalidException;
    Role handleCreateRole(CreateRoleRequestDto roleDTO);
    Optional<Role> handleFindById(long id);
    Role handleFindByName(String name) throws InvalidException;
    Role handleUpdateRole(UpdateRoleRequestDto roleDTO);
    ResultPaginateDto handleGetRoles(Specification<Role> spec, Pageable pageable);
    void handleDeleteRoleById(long id);
    Role handleGetRoleById(long id) throws InvalidException;
}
