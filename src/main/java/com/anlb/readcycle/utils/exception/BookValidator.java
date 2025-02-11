package com.anlb.readcycle.utils.exception;

import org.apache.commons.lang3.StringUtils;
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
        if (StringUtils.isBlank(book.getCategory())) {
            context.buildConstraintViolationWithTemplate("Category is required")
                    .addPropertyNode("category")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= book.getCategory().length()) {
            context.buildConstraintViolationWithTemplate("First name must be greater than or equal 2")
                    .addPropertyNode("category")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation title
        if (StringUtils.isBlank(book.getTitle())) {
            context.buildConstraintViolationWithTemplate("Title is required")
                    .addPropertyNode("title")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= book.getTitle().length()) {
            context.buildConstraintViolationWithTemplate("Title must be greater than 2")
                    .addPropertyNode("title")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation author
        if (StringUtils.isBlank(book.getAuthor())) {
            context.buildConstraintViolationWithTemplate("Author is required")
                    .addPropertyNode("author")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= book.getAuthor().length()) {
            context.buildConstraintViolationWithTemplate("Author must be greater than 2")
                    .addPropertyNode("author")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // validation publisher
        if (StringUtils.isBlank(book.getPublisher())) {
            context.buildConstraintViolationWithTemplate("Publisher is required")
                    .addPropertyNode("publisher")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        } else if (2 >= book.getPublisher().length()) {
            context.buildConstraintViolationWithTemplate("Publisher must be greater than 2")
                    .addPropertyNode("publisher")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
