package com.anlb.readcycle.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {
    private long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String email;
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;
    @NotBlank(message = "Role is required")
    private String role;
}
