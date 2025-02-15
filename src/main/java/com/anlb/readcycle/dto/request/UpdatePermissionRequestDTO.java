package com.anlb.readcycle.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePermissionRequestDTO {
    private long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Api path is required")
    private String apiPath;
    @NotBlank(message = "Method is required")
    private String method;
    @NotBlank(message = "Module is required")
    private String module;
}
