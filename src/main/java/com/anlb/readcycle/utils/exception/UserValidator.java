package com.anlb.readcycle.utils.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.dto.request.CreateUserRequestDTO;
import com.anlb.readcycle.service.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidator implements ConstraintValidator<UserChecked, CreateUserRequestDTO> {

    private final UserService userService;

    @Override
    public boolean isValid(CreateUserRequestDTO user, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation first name
        if (StringUtils.isBlank(user.getFirstName())) {
            context.buildConstraintViolationWithTemplate("First name is required")
                    .addPropertyNode("firstName")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (1 >= user.getFirstName().length()) {
            context.buildConstraintViolationWithTemplate("First name must be greater than 1")
                    .addPropertyNode("firstName")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation last name
        if (StringUtils.isBlank(user.getLastName())) {
            context.buildConstraintViolationWithTemplate("Last name is required")
                    .addPropertyNode("lastName")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (1 >= user.getLastName().length()) {
            context.buildConstraintViolationWithTemplate("Last name must be greater than 1")
                    .addPropertyNode("lastName")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation email
        if (StringUtils.isBlank(user.getEmail())) {
            context.buildConstraintViolationWithTemplate("Email is required")
                    .addPropertyNode("email")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (!(user.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            context.buildConstraintViolationWithTemplate("Invalid email format")
                    .addPropertyNode("email")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (this.userService.handleCheckExistsByEmail(user.getEmail())) {
            context.buildConstraintViolationWithTemplate("Email already exists")
                    .addPropertyNode("email")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation date of birth
        if (StringUtils.isBlank(user.getDateOfBirth())) {
            context.buildConstraintViolationWithTemplate("Date of birth is required")
                    .addPropertyNode("dateOfBirth")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (!isValidDateFormat(user.getDateOfBirth())) {
            context.buildConstraintViolationWithTemplate("Invalid date of birth format")
                    .addPropertyNode("dateOfBirth")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate yob = LocalDate.parse(user.getDateOfBirth(), formatter);
            LocalDate today = LocalDate.now();

            if (!yob.isBefore(today)) {
                context.buildConstraintViolationWithTemplate("Date of birth cannot be equal to or greater than the current date")
                        .addPropertyNode("dateOfBirth")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        // validation password
        if (StringUtils.isBlank(user.getPassword())) {
            context.buildConstraintViolationWithTemplate("Password is required")
                    .addPropertyNode("password")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= user.getPassword().length()) {
            context.buildConstraintViolationWithTemplate("Password must be greater than or equal to 3")
                    .addPropertyNode("password")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

         // validation confirm password
        if (StringUtils.isBlank(user.getConfirmPassword())) {
            context.buildConstraintViolationWithTemplate("Confirm password is required")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= user.getConfirmPassword().length()) {
            context.buildConstraintViolationWithTemplate("Confirm password must be greater than or equal to 3")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // compare password and confirm-password
        if (valid) {
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                context.buildConstraintViolationWithTemplate("Incorrect password, please check again")
                        .addPropertyNode("password")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                valid = false;
            }
        }

        // validation role
        if (StringUtils.isBlank(user.getRole())) {
            context.buildConstraintViolationWithTemplate("Role is required")
                    .addPropertyNode("role")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
    }

    public static boolean isValidDateFormat(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            return date.equals(parsedDate.format(formatter));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
