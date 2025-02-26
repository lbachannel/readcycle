package com.anlb.readcycle.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.CreateUserRequestDto;
import com.anlb.readcycle.dto.request.RegisterRequestDto;
import com.anlb.readcycle.dto.response.CreateUserResponseDto;
import com.anlb.readcycle.dto.response.LoginResponseDto;
import com.anlb.readcycle.dto.response.LoginResponseDto.UserLogin;
import com.anlb.readcycle.service.IRoleService;
import com.anlb.readcycle.dto.response.RegisterResponseDto;
import com.anlb.readcycle.dto.response.UpdateUserResponseDto;
import com.anlb.readcycle.dto.response.UserResponseDto;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.anlb.readcycle.utils.exception.RegisterValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMapper {
    
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final SecurityUtil securityUtil;

    /**
     * Converts a {@link RegisterRequestDto} object to a {@link User} entity.
     * 
     * This method extracts user details from the DTO and creates a new {@link User} entity.
     * It also hashes the password before storing it.
     * If the provided date of birth is in a valid format, it is parsed into a {@link LocalDate}.
     * The user's role is set to the default role with ID 2.
     *
     * @param registerDTO The {@link RegisterRequestDto} containing user registration details.
     * @return A {@link User} entity populated with data from the DTO.
     */
    public User convertRegisterDTOToUser(RegisterRequestDto registerDTO) {
        User user = new User();
        user.setName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        // hash password
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        user.setPassword(registerDTO.getPassword());
        if (RegisterValidator.isValidDateFormat(registerDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(registerDTO.getDateOfBirth(), formatter));
        }
        user.setRole(this.roleService.handleFindById(2).get());

        return user;
    }

    /**
     * Converts a {@link CreateUserRequestDto} object to a {@link User} entity.
     *
     * @param userDTO the DTO containing user details
     * @return a {@link User} entity with the provided details
     * @throws InvalidException if any validation fails
     */
    public User convertCreateUserRequestDTOToUser(CreateUserRequestDto userDTO) throws InvalidException {
        User user = new User();
        user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        if (RegisterValidator.isValidDateFormat(userDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
        }
        user.setRole(this.roleService.handleFindByName(userDTO.getRole()));
        return user;
    }

    /**
     * Converts a {@link User} entity to a {@link RegisterResponseDto}.
     *
     * This method extracts relevant user information and maps it to a DTO
     * for registration response purposes.
     *
     * @param user The {@link User} entity to be converted.
     * @return A {@link RegisterResponseDto} containing user details.
     */
    public RegisterResponseDto convertUserToRegisterResponseDTO(User user) {
        RegisterResponseDto response = new RegisterResponseDto();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setVerificationEmailToken(user.getVerificationEmailToken());
        return response;
    }

    /**
     * Converts a {@link User} entity to a {@link CreateUserResponseDto}.
     *
     * This method extracts relevant user information and maps it to a DTO
     * for user creation response purposes.
     *
     * @param user The {@link User} entity to be converted.
     * @return A {@link CreateUserResponseDto} containing user details.
     */
    public CreateUserResponseDto convertUserToCreateResponseDTO(User user) {
        CreateUserResponseDto response = new CreateUserResponseDto();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCreatedAt(user.getCreatedAt());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        return response;
    }

    /**
     * Converts a {@link User} entity to a {@link UserResponseDto}.
     *
     * @param user The {@link User} entity to convert.
     * @return A {@link UserResponseDto} containing user details.
     */
    public UserResponseDto convertUserToUserResponseDTO(User user) {
        UserResponseDto response = new UserResponseDto();
        UserResponseDto.RoleUser roleUser = new UserResponseDto.RoleUser();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCreatedAt(user.getCreatedAt());
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            response.setRole(roleUser);
        }
        response.setActive(user.isActive());
        return response;
    }

    /**
     * Converts a User object into a UserLogin object.
     * 
     * @param dbUser The User object containing user details.
     * @return A UserLogin object with essential user information.
     */
    public UserLogin convertUserToUserLogin(User dbUser) {
        UserLogin user = new UserLogin();
        user.setId(dbUser.getId());
        user.setEmail(dbUser.getEmail());
        user.setName(dbUser.getName());
        user.setRole(dbUser.getRole());
        return user;
    }

    /**
     * Converts a {@link User} entity into a {@link LoginResponseDto}, including user details and an access token.
     *
     * @param dbUser        The authenticated user entity.
     * @param authentication The authentication object containing user credentials.
     * @return A {@link LoginResponseDto} containing user details and an access token.
     */
    public LoginResponseDto convertUserToLoginResponseDTO(User dbUser, Authentication authentication) {
        UserLogin user = new UserLogin();
        user.setId(dbUser.getId());
        user.setEmail(dbUser.getEmail());
        user.setName(dbUser.getName());
        user.setRole(dbUser.getRole());
        LoginResponseDto response = new LoginResponseDto();
        response.setUser(user);

        // set access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), response);
        response.setAccessToken(accessToken);
        return response;
    }

    /**
     * Converts a User entity to an UpdateUserResponseDTO.
     *
     * @param updateUser the User object to be converted
     * @return an UpdateUserResponseDTO containing the user's details
     */
    public UpdateUserResponseDto convertUserToUpdateUserResponseDTO(User updateUser) {
        UpdateUserResponseDto response = new UpdateUserResponseDto();
        response.setId(updateUser.getId());
        response.setName(updateUser.getName());
        response.setEmail(updateUser.getEmail());
        response.setDateOfBirth(updateUser.getDateOfBirth());
        response.setCreatedAt(updateUser.getCreatedAt());
        response.setRole(updateUser.getRole());
        response.setActive(updateUser.isActive());
        return response;
    }
}
