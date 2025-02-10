package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.request.CreateUserRequestDTO;
import com.anlb.readcycle.domain.dto.request.RegisterRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateUserResponseDTO;
import com.anlb.readcycle.domain.dto.response.RegisterResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.service.EmailService;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;
    
    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginateDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(this.userService.handleGetAllUsers(spec, pageable));
    }

    @PostMapping("/user/register")
    @ApiMessage("Register account")
    public ResponseEntity<RegisterResponseDTO> registerMember(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        // hash password
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        // convert DTO -> User
        User newUser = this.userService.registerDTOtoUser(registerDTO);
        // save user
        newUser = this.userService.handleRegisterMember(newUser);
        // send email
        this.emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertUserToRegisterResponseDTO(newUser));
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<CreateUserResponseDTO> createNewUser(@Valid @RequestBody CreateUserRequestDTO userDTO) {
        // hash password
        String hashPassword = this.passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        // convert DTO -> User
        User newUser = this.userService.convertCreateUserRequestDTOToUser(userDTO);
        // save user
        newUser = this.userService.handleCreateUser(newUser);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.userService.convertUserToCreateResponseDTO(newUser));
    }
}
