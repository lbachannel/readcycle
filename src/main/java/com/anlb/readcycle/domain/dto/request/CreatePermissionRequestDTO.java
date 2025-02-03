package com.anlb.readcycle.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequestDTO {
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
