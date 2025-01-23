package com.anlb.readcycle.utils.exception;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.dto.LoginDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class LoginValidator implements ConstraintValidator<LoginChecked, LoginDTO>{

    @Override
    public boolean isValid(LoginDTO account, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation username
        if (account.getUsername().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Please enter username")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (account.getUsername().length() < 10) {
            context.buildConstraintViolationWithTemplate("Username must be greater than or equal 10")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (!(account.getUsername().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
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
        } else if (account.getPassword().length() <= 2) {
            context.buildConstraintViolationWithTemplate("Password must be greater than or equal to 3")
                    .addPropertyNode("password")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
    }
    
}
