package com.anlb.readcycle.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.RegisterDTO;
import com.anlb.readcycle.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@Valid @RequestBody RegisterDTO registerDTO) {
        User newUser = this.userService.registerDTOtoUser(registerDTO);
        this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    
}
