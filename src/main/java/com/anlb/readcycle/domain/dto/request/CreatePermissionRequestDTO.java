package com.anlb.readcycle.domain.dto.request;

import com.anlb.readcycle.utils.exception.PermissionChecked;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PermissionChecked
public class CreatePermissionRequestDTO {
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
