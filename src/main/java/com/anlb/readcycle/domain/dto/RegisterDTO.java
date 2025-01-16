package com.anlb.readcycle.domain.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "firstname is required")
    private String firstName;
    @NotBlank(message = "lastname is required")
    private String lastName;
    @Email(message = "User email format is incorrect")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @NotNull(message = "date of birth is required")
    private LocalDate dateOfBirth;
    @NotBlank(message = "confirm password is required")
    private String confirmPassword;
}
