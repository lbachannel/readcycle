package com.anlb.readcycle.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Api path is required")
    private String apiPath;
    @NotBlank(message = "Method path is required")
    private String method;
    @NotBlank(message = "Module is required")
    private String module;
}
