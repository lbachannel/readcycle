package com.anlb.readcycle.domain.dto;

import com.anlb.readcycle.utils.exception.RegisterChecked;

import lombok.Data;

@Data
@RegisterChecked
public class RegisterDTO {
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String password;
    
    private String dateOfBirth;
    
    private String confirmPassword;
}
