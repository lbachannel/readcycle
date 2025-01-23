package com.anlb.readcycle.utils.exception;

import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.dto.request.CreateBookRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Service
public class BookValidator implements ConstraintValidator<BookChecked, CreateBookRequestDTO> {

    @Override
    public boolean isValid(CreateBookRequestDTO book, ConstraintValidatorContext context) {
        boolean valid = true;

        // validation category
        if (book.getCategory().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Category is required")
                    .addPropertyNode("category")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (book.getCategory().length() <= 2) {
            context.buildConstraintViolationWithTemplate("First name must be greater than 2")
                    .addPropertyNode("category")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation title
        if (book.getTitle().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Title is required")
                    .addPropertyNode("title")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (book.getTitle().length() <= 2) {
            context.buildConstraintViolationWithTemplate("Title must be greater than 2")
                    .addPropertyNode("title")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation author
        if (book.getAuthor().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Author is required")
                    .addPropertyNode("author")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (book.getAuthor().length() <= 2) {
            context.buildConstraintViolationWithTemplate("Author must be greater than 2")
                    .addPropertyNode("author")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation publisher
        if (book.getPublisher().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Publisher is required")
                    .addPropertyNode("publisher")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (book.getPublisher().length() <= 2) {
            context.buildConstraintViolationWithTemplate("Publisher must be greater than 2")
                    .addPropertyNode("publisher")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
