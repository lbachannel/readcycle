package com.anlb.readcycle.domain.dto.response;

import java.time.LocalDate;

import com.anlb.readcycle.domain.Role;

import lombok.Data;

@Data
public class CreateUserResponseDTO {
    private long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;

    private Role role;
}
