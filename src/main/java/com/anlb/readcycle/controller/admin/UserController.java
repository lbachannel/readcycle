package com.anlb.readcycle.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.RegisterDTO;
import com.anlb.readcycle.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@Valid @RequestBody RegisterDTO registerDTO) {
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        User newUser = this.userService.registerDTOtoUser(registerDTO);
        this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    
}
