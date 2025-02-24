package com.anlb.readcycle.dto.response;

import java.time.Instant;
import java.util.List;

import com.anlb.readcycle.domain.Permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<Permission> permissions;
}
