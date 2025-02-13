package com.anlb.readcycle.utils.exception;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.anlb.readcycle.domain.dto.response.ResultResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponseDTO<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        ResultResponseDTO<Object> res = new ResultResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());

        String errorDetails = ex.getBody() != null ? ex.getBody().getDetail() : "Validation failed";
        res.setError(errorDetails);

        List<String> errors = fieldErrors.stream()
                                .map(f -> Objects.requireNonNullElse(f.getDefaultMessage(), "Unknown error"))
                                .collect(Collectors.toList());
        res.setMessage(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // handle verify email exception
    @ExceptionHandler(value = {
        InvalidException.class
    })
    public ResponseEntity<ResultResponseDTO<Object>> handleInvalidException(Exception ex) {
        ResultResponseDTO<Object> response = new ResultResponseDTO<Object>();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setError(ex.getMessage());
        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = {
        StorageException.class,
    })
    public ResponseEntity<ResultResponseDTO<Object>> handleFileUploadException(Exception ex) {
        ResultResponseDTO<Object> res = new ResultResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception upload file...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}