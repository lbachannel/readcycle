package com.anlb.readcycle.dto.request;

import java.util.List;

import com.anlb.readcycle.domain.Permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    private boolean active;
    private List<Permission> permissions;
}
