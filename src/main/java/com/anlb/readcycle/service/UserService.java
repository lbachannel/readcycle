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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.UpdateUserRequestDTO;
import com.anlb.readcycle.dto.response.LoginResponseDTO;
import com.anlb.readcycle.dto.response.ResultPaginateDTO;
import com.anlb.readcycle.dto.response.UserResponseDTO;
import com.anlb.readcycle.mapper.UserMapper;
import com.anlb.readcycle.dto.response.LoginResponseDTO.UserGetAccount;
import com.anlb.readcycle.dto.response.LoginResponseDTO.UserLogin;
import com.anlb.readcycle.repository.UserRepository;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;
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
    private final UserMapper userMapper;

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
     *
     */
    public User handleGetUserByUsername(String username) throws InvalidException {
        User user = this.userRepository.findByEmail(username);
        if (user == null) {
            throw new InvalidException("Bad credentials");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidException("Your account has not been verified");
        }
        
        return user;
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
     * Updates the refresh token of the currently logged-in user.
     *
     * This method retrieves the email of the currently logged-in user using {@link SecurityUtil#getCurrentUserLogin()}.
     * If the email is not present or is blank, an {@link InvalidException} is thrown. Otherwise, the corresponding
     * {@link User} is fetched from the database, and their refresh token is updated and saved.
     *
     * @param refreshToken the new refresh token to be set for the user
     * @throws InvalidException if the current user's email is not available (indicating an invalid access token)
     */
    public void handleUpdateRefreshTokenIntoUser(String refreshToken, String email) throws InvalidException {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    /**
     * Validates a refresh token and email against the stored user data.
     *
     * @param refreshToken the refresh token to verify
     * @param decodedToken the decoded JWT containing the user's subject (email or username)
     * @throws InvalidException if no user is found with the provided refresh token and email
     */
    public void handleGetUserByRefreshTokenAndEmail(String refreshToken, Jwt decodedToken) throws InvalidException {  
        User dbUser = this.userRepository.findByRefreshTokenAndEmail(refreshToken, decodedToken.getSubject());
        if (dbUser == null) {
            throw new InvalidException("Refresh token is not valid");
        }
    }

    /**
     * Generates a {@link LoginResponseDTO} from a decoded JWT token.
     *
     * @param decodedToken the decoded JWT containing the user's subject (email or username)
     * @return a {@link LoginResponseDTO} containing user details and an access token
     * @throws InvalidException if the user associated with the token does not exist
     */
    public LoginResponseDTO generateLoginResponseFromToken (Jwt decodedToken) throws InvalidException {
        User dbUser = this.handleGetUserByUsername(decodedToken.getSubject());
        UserLogin user = new UserLogin(
            dbUser.getId(),
            dbUser.getEmail(),
            dbUser.getName(),
            dbUser.getRole()
        );

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUser(user);

        // set access token
        String accessToken = this.securityUtil.createAccessToken(decodedToken.getSubject(), response);
        response.setAccessToken(accessToken);
        return response;
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
                                            .map(item -> this.userMapper.convertUserToUserResponseDTO(item))
                                            .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    /**
     * Retrieves the currently authenticated user's account information.
     *
     * @return A {@link UserGetAccount} containing the authenticated user's details.
     * @throws InvalidException If the user is not found or their account is invalid.
     */
    public UserGetAccount getCurrentUserAccount() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                        ? SecurityUtil.getCurrentUserLogin().get() : "";
        User dbUser = this.handleGetUserByUsername(email);
        UserLogin userLogin = this.userMapper.convertUserToUserLogin(dbUser);
        return this.convertUserLoginToUserGetAccount(userLogin);
    }

    /**
     * Converts a UserLogin object into a UserGetAccount object.
     * 
     * @param userLogin The UserLogin object containing user details.
     * @return A UserGetAccount object that encapsulates the user information.
     */
    public UserGetAccount convertUserLoginToUserGetAccount(UserLogin userLogin) {
        UserGetAccount userGetAccount = new UserGetAccount();
        userGetAccount.setUser(userLogin);
        return userGetAccount;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user to be retrieved
     * @return the User object if found
     * @throws InvalidException if no user with the given ID exists
     */
    public User handleGetUserById(long id) throws InvalidException {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new InvalidException("User with id: " + id + " does not exists");
        }
        return user;
    }

    /**
     * Updates an existing user's details based on the provided request data.
     *
     * @param reqUser the request DTO containing updated user information
     * @return the updated User object
     * @throws InvalidException if the user with the given ID does not exist
     */
    public User handleUpdateUser(UpdateUserRequestDTO reqUser) throws InvalidException {
        User updateUser = this.handleGetUserById(reqUser.getId());
        updateUser.setName(reqUser.getName());
        updateUser.setEmail(reqUser.getEmail());
        if (RegisterValidator.isValidDateFormat(reqUser.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            updateUser.setDateOfBirth(LocalDate.parse(reqUser.getDateOfBirth(), formatter));
        }
        updateUser.setRole(this.roleService.handleFindByName(reqUser.getRole()));
        return updateUser;
    }

    /**
     * Deletes a user by their ID, ensuring that a user cannot delete themselves.
     *
     * @param id The ID of the user to be deleted.
     * @throws InvalidException If the access token is invalid or if the user attempts to delete themselves.
     */
    public void handleDeleteUserById(long id) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = this.handleGetUserById(id);
        if (user.getEmail().equals(email)) {
            throw new InvalidException("You can not delete yourself");
        }
        this.userRepository.deleteById(id);
    }
}
