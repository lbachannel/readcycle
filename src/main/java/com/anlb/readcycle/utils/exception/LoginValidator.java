package com.anlb.readcycle.utils.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.dto.request.LoginRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class LoginValidator implements ConstraintValidator<LoginChecked, LoginRequestDto>{

    @Override
    public boolean isValid(LoginRequestDto account, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation username
        if (StringUtils.isBlank(account.getUsername())) {
            context.buildConstraintViolationWithTemplate("Please enter username")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (10 > account.getUsername().length()) {
            context.buildConstraintViolationWithTemplate("Username must be greater than 10")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } 
        else if (!(account.getUsername().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            context.buildConstraintViolationWithTemplate("Invalid email format")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        
        // validation password
        if (StringUtils.isBlank(account.getPassword())) {
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
