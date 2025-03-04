package com.anlb.readcycle.dto.response;

import java.time.Instant;
import java.util.List;

import com.anlb.readcycle.domain.Permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private String createdBy;
    private List<Permission> permissions;
}
