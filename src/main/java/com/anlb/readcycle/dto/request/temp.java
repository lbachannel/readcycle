package com.anlb.readcycle.dto.request;

import com.anlb.readcycle.utils.exception.UserChecked;

import lombok.Data;

@Data
@UserChecked
public class CreateUserRequestDto {
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String password;
    
    private String dateOfBirth;
    
    private String confirmPassword;

    private String role;
}
