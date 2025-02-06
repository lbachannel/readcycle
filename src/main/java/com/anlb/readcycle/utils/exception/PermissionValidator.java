package com.anlb.readcycle.utils.exception;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.dto.request.CreatePermissionRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class PermissionValidator implements ConstraintValidator<PermissionChecked, CreatePermissionRequestDTO>{
    @Override
    public boolean isValid(CreatePermissionRequestDTO permission, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation name
        if (permission.getName().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Name is required")
                    .addPropertyNode("name")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation api path
        if (permission.getApiPath().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Api path is required")
                    .addPropertyNode("apiPath")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation method
        if (permission.getMethod().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Method is required")
                    .addPropertyNode("method")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        //validation module
        if (permission.getMethod().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Module is required")
                    .addPropertyNode("module")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
