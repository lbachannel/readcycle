package com.anlb.readcycle.utils.exception;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.dto.request.LoginRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class LoginValidator implements ConstraintValidator<LoginChecked, LoginRequestDTO>{

    @Override
    public boolean isValid(LoginRequestDTO account, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation username
        if (account.getUsername().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Please enter username")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (10 > account.getUsername().length()) {
            context.buildConstraintViolationWithTemplate("Username must be greater than or equal 10")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } 
        else if (!("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".matches(account.getUsername()))) {
            context.buildConstraintViolationWithTemplate("Invalid email format")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        
        // validation password
        if (account.getPassword().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Please enter password")
                    .addPropertyNode("password")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= account.getPassword().length()) {
            context.buildConstraintViolationWithTemplate("Password must be greater than or equal to 3")
                    .addPropertyNode("password")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
    }
    
}
