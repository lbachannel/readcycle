package com.anlb.readcycle.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateUserRequestDTO;
import com.anlb.readcycle.dto.request.RegisterRequestDTO;
import com.anlb.readcycle.dto.request.UpdateUserRequestDTO;
import com.anlb.readcycle.dto.response.CreateUserResponseDTO;
import com.anlb.readcycle.dto.response.RegisterResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UpdateUserResponseDTO;
import com.anlb.readcycle.mapper.UserMapper;
import com.anlb.readcycle.service.EmailService;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final EmailService emailService;
    private final UserService userService;
    private final UserMapper userMapper;
    
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
        // convert DTO -> User
        User newUser = this.userMapper.convertRegisterDTOToUser(registerDTO);
        // save user
        newUser = this.userService.handleRegisterMember(newUser);
        // send email
        this.emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email");
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.userMapper.convertUserToRegisterResponseDTO(newUser));
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<CreateUserResponseDTO> createNewUser(@Valid @RequestBody CreateUserRequestDTO userDTO) throws InvalidException {
        // convert DTO -> User
        User newUser = this.userMapper.convertCreateUserRequestDTOToUser(userDTO);
        // save user
        newUser = this.userService.handleCreateUser(newUser);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.userMapper.convertUserToCreateResponseDTO(newUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@Valid @RequestBody UpdateUserRequestDTO reqUser) throws InvalidException {
        User updateUser = this.userService.handleUpdateUser(reqUser);
        return ResponseEntity
                    .ok(this.userMapper.convertUserToUpdateUserResponseDTO(updateUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<User> deleteUser(@PathVariable("id") long id) throws InvalidException {
        User user = this.userService.handleGetUserById(id);
        this.userService.handleDeleteUserById(user.getId());
        return ResponseEntity.ok().body(user);
    }
}
