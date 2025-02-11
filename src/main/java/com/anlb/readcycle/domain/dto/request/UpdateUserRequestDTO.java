package com.anlb.readcycle.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {
    private long id;
    private String name;
    private String email;
    private String dateOfBirth;
    private String role;
}
