package com.anlb.readcycle.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.request.CreateUserRequestDTO;
import com.anlb.readcycle.domain.dto.request.RegisterRequestDTO;
import com.anlb.readcycle.domain.dto.response.CreateUserResponseDTO;
import com.anlb.readcycle.domain.dto.response.RegisterResponseDTO;
import com.anlb.readcycle.domain.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.domain.dto.response.UserResponseDTO;
import com.anlb.readcycle.repository.UserRepository;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.RegisterValidator;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final JwtDecoder jwtDecoder;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Converts a {@link RegisterRequestDTO} object to a {@link User} entity.
     * 
     * This method extracts user details from the DTO and creates a new {@link User} entity.
     * It also hashes the password before storing it.
     * If the provided date of birth is in a valid format, it is parsed into a {@link LocalDate}.
     * The user's role is set to the default role with ID 2.
     *
     * @param registerDTO The {@link RegisterRequestDTO} containing user registration details.
     * @return A {@link User} entity populated with data from the DTO.
     */
    public User convertRegisterDTOToUser(RegisterRequestDTO registerDTO) {
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
     * Converts a {@link CreateUserRequestDTO} object to a {@link User} entity.
     *
     * This method extracts user details from the DTO and creates a new {@link User} entity.
     * It also hashes the password before storing it.
     * If the provided date of birth is in a valid format, it is parsed into a {@link LocalDate}.
     * The user's role is determined based on the role name provided in the DTO.
     *
     * @param userDTO The {@link CreateUserRequestDTO} containing user registration details.
     * @return A {@link User} entity populated with data from the DTO.
     */
    public User convertCreateUserRequestDTOToUser(CreateUserRequestDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        // hash password
        String hashPassword = this.passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        user.setPassword(userDTO.getPassword());
        if (RegisterValidator.isValidDateFormat(userDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
        }
        user.setRole(this.roleService.handleFindByName(userDTO.getRole()));
        return user;
    }

    /**
     * Converts a {@link User} entity to a {@link RegisterResponseDTO}.
     *
     * This method extracts relevant user information and maps it to a DTO
     * for registration response purposes.
     *
     * @param user The {@link User} entity to be converted.
     * @return A {@link RegisterResponseDTO} containing user details.
     */
    public RegisterResponseDTO convertUserToRegisterResponseDTO(User user) {
        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setVerificationEmailToken(user.getVerificationEmailToken());
        return response;
    }

    /**
     * Converts a {@link User} entity to a {@link CreateUserResponseDTO}.
     *
     * This method extracts relevant user information and maps it to a DTO
     * for user creation response purposes.
     *
     * @param user The {@link User} entity to be converted.
     * @return A {@link CreateUserResponseDTO} containing user details.
     */
    public CreateUserResponseDTO convertUserToCreateResponseDTO(User user) {
        CreateUserResponseDTO response = new CreateUserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRole(user.getRole());
        return response;
    }

    /**
     * Handles the registration process for a new member.
     *
     * This method generates an email verification token, sets the email verification
     * status to false, assigns the token to the user, and then saves the user to the database.
     *
     * @param user The {@link User} entity to be registered.
     * @return The saved {@link User} entity with the verification token.
     */
    public User handleRegisterMember(User user) {
        String verifyEmailToken = this.securityUtil.createVerifyEmailToken(user.getEmail());
        user.setEmailVerified(false);
        user.setVerificationEmailToken(verifyEmailToken);
        return this.userRepository.save(user);
    }

    /**
     * Creates and saves a new user in the system.
     * 
     * This method sets the user's email verification status to true and persists the user entity to the database.
     *
     * @param user The {@link User} entity to be created.
     * @return The saved {@link User} entity.
     */
    public User handleCreateUser(User user) {
        user.setEmailVerified(true);
        return this.userRepository.save(user);
    }

    /**
     * Checks if a user exists in the system by their email.
     *
     * @param email The email address to check.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    public boolean handleCheckExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    /**
     * Retrieves a user by their username (email).
     *
     * @param username The username (email) of the user.
     * @return The {@code User} object corresponding to the given username.
     */
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    /**
     * Verifies a user's email based on the provided verification token.
     * If the token is valid, the user's email is marked as verified and the token is removed.
     *
     * @param token The verification email token used to verify the user's email.
     * @return The updated {@link User} object with email verification set to {@code true}.
     * @throws NoSuchElementException if the token is not found in the repository.
     */
    public User handleVerifyEmail(String token) {
        User user = this.userRepository.findByVerificationEmailToken(token).get();
        user.setEmailVerified(true);
        user.setVerificationEmailToken(null);
        return this.userRepository.save(user);
    }

    /**
     * Validates a JWT token by decoding it and checking its expiration time.
     * If the token is expired or invalid, the method returns {@code false}.
     *
     * @param token The JWT token to be validated.
     * @return {@code true} if the token is valid and not expired, otherwise {@code false}.
     * @throws JwtException if the token is malformed or cannot be decoded.
     */
    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expirationTime = jwt.getExpiresAt();
            if (expirationTime.isBefore(Instant.now())) {
                return false;
            }
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the email claim from a given JWT token.
     * If the token is invalid or an error occurs during decoding, the method returns {@code null}.
     *
     * @param token The JWT token from which the email will be extracted.
     * @return The email contained in the token, or {@code null} if extraction fails.
     */
    public String extractEmailFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("email");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deletes a user from the repository based on their email.
     *
     * @param email The email of the user to be deleted.
     */
    public void handleDeleteUserByEmail(String email) {
        this.userRepository.deleteByEmail(email);
    }

    /**
     * Updates the refresh token for a user based on their email.
     *
     * @param refreshToken The new refresh token to be stored.
     * @param email        The email of the user whose refresh token is being updated.
     */
    public void handleUpdateRefreshTokenIntoUser(String refreshToken, String email) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    /**
     * Retrieves a user by their refresh token and email.
     *
     * @param refreshToken The refresh token associated with the user.
     * @param email        The email of the user.
     * @return The user matching the given refresh token and email, or null if not found.
     */
    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    /**
     * Retrieves a paginated list of users based on the provided specification.
     *
     * @param spec     The specification to filter users.
     * @param pageable The pagination and sorting information.
     * @return A {@link ResultPaginateDTO} containing the paginated user list and metadata.
     */
    public ResultPaginateDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginateDTO rs = new ResultPaginateDTO();
        ResultPaginateDTO.Meta mt = new ResultPaginateDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<UserResponseDTO> listUser = pageUser.getContent()
                                            .stream()
                                            .map(item -> this.convertUserToUserResponseDTO(item))
                                            .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    /**
     * Converts a {@link User} entity to a {@link UserResponseDTO}.
     *
     * @param user The {@link User} entity to convert.
     * @return A {@link UserResponseDTO} containing user details.
     */
    public UserResponseDTO convertUserToUserResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        UserResponseDTO.RoleUser roleUser = new UserResponseDTO.RoleUser();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setDateOfBirth(user.getDateOfBirth());
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            response.setRole(roleUser);
        }
        return response;
    }
}
