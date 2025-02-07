package com.anlb.readcycle.domain.dto.request;

import lombok.Data;

@Data
public class CreateUserRequestDTO {
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String password;
    
    private String dateOfBirth;
    
    private String confirmPassword;

    private String role;
}
