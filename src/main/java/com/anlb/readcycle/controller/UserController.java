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
import com.anlb.readcycle.dto.request.CreateUserRequestDto;
import com.anlb.readcycle.dto.request.RegisterRequestDto;
import com.anlb.readcycle.dto.request.UpdateUserRequestDto;
import com.anlb.readcycle.dto.response.CreateUserResponseDto;
import com.anlb.readcycle.dto.response.RegisterResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.UpdateUserResponseDto;
import com.anlb.readcycle.mapper.UserMapper;
import com.anlb.readcycle.service.IEmailService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final IEmailService emailService;
    private final IUserService userService;
    private final UserMapper userMapper;

    /**
     * {@code GET  /users} : Retrieves a paginated list of all users
     *                       based on the provided filters.
     *
     * @param spec     The filter criteria for querying users.
     * @param pageable The pagination information.
     * @return A {@link ResponseEntity} containing a paginated list of users.
     */
    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginateDto> getAllUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.handleGetAllUsers(spec, pageable));
    }

    /**
     * {@code POST  /user/register} : Registers a new user account.
     *
     * @param registerDto The registration request data.
     * @return A {@link ResponseEntity} containing the registered user's details.
     */
    @PostMapping("/user/register")
    @ApiMessage("Register account")
    public ResponseEntity<RegisterResponseDto> registerMember(@Valid @RequestBody RegisterRequestDto registerDto) {
        // convert DTO -> User
        User newUser = userMapper.convertRegisterDtoToUser(registerDto);
        // save user
        newUser = userService.handleRegisterMember(newUser);
        // send email
        emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email-user");
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userMapper.convertUserToRegisterResponseDto(newUser));
    }

    /**
     * {@code POST  /users} : Creates a new user.
     *
     * @param userDto The request data containing user details.
     * @return A {@link ResponseEntity} containing the created user's details.
     * @throws InvalidException If the user creation fails due to invalid data.
     */
    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<CreateUserResponseDto> createNewUser(@Valid @RequestBody CreateUserRequestDto userDto) throws InvalidException {
        // convert DTO -> User
        User newUser = userMapper.convertCreateUserRequestDtoToUser(userDto);
        // save user
        newUser = userService.handleCreateUser(newUser);
        // send email
        emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email");
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userMapper.convertUserToCreateResponseDto(newUser));
    }

    /**
     * {@code PUT  /users} : Updates an existing user.
     *
     * @param reqUser The request data containing updated user details.
     * @return A {@link ResponseEntity} containing the updated user's details.
     * @throws InvalidException If the update fails due to invalid data or the user does not exist.
     */
    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<UpdateUserResponseDto> updateUser(@Valid @RequestBody UpdateUserRequestDto reqUser) throws InvalidException {
        User updateUser = userService.handleUpdateUser(reqUser);
        return ResponseEntity
                    .ok(userMapper.convertUserToUpdateUserResponseDto(updateUser));
    }

    /**
     * {@code DELETE  /users/{id}} : Deletes a user by their ID.
     *
     * @param id The ID of the user to be deleted.
     * @return A {@link ResponseEntity} containing the deleted user's details.
     * @throws InvalidException If the user does not exist or cannot be deleted.
     */
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<User> deleteUser(@PathVariable("id") long id) throws InvalidException {
        User user = userService.handleGetUserById(id);
        userService.handleDeleteUserById(user.getId());
        return ResponseEntity.ok().body(user);
    }

    /**
     * {@code DELETE /users/{id}} : Deletes a user by its ID.
     *
     * @return A {@link ResponseEntity} containing the deleted {@link User}.
     * @throws InvalidException If the user with the given ID is not found or the operation is invalid.
     */
    @PutMapping("/users/{id}")
    @ApiMessage("Toggle soft delete a user")
    public ResponseEntity<User> toggleSoftDeleteUser(@PathVariable("id") long id) throws InvalidException {
        User isDeletedUser = userService.handleGetUserById(id);
        isDeletedUser = userService.handleSoftDelete(isDeletedUser.getId());
        return ResponseEntity
                    .ok()
                    .body(isDeletedUser);
    }
}
