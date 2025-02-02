package com.anlb.readcycle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.request.RegisterRequestDTO;
import com.anlb.readcycle.domain.dto.response.RegisterResponseDTO;
import com.anlb.readcycle.service.EmailService;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.anotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;

    public UserController(PasswordEncoder passwordEncoder, EmailService emailService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/user/register")
    @ApiMessage("Register account")
    public ResponseEntity<RegisterResponseDTO> createNewUser(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        // hash password
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        // convert DTO -> User
        User newUser = this.userService.registerDTOtoUser(registerDTO);
        // save user
        newUser = this.userService.handleCreateUser(newUser);
        // send email
        this.emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertUserToRegisterResponseDTO(newUser));
    }
}
