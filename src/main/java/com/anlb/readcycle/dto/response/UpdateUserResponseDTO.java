package com.anlb.readcycle.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import com.anlb.readcycle.domain.Role;

import lombok.Data;

@Data
public class UpdateUserResponseDto {
    private long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private Instant createdAt;

    private Role role;
    private boolean active;
}
