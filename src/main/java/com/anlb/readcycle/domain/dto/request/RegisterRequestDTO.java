package com.anlb.readcycle.domain.dto.request;

import com.anlb.readcycle.utils.exception.RegisterChecked;

import lombok.Data;

@Data
@RegisterChecked
public class RegisterRequestDTO {
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String password;
    
    private String dateOfBirth;
    
    private String confirmPassword;
}
