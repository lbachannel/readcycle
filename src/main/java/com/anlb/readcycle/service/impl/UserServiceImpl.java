package com.anlb.readcycle.service.impl;

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
import com.anlb.readcycle.dto.request.ChangePasswordRequestDto;
import com.anlb.readcycle.dto.request.UpdateUserRequestDto;
import com.anlb.readcycle.dto.response.LoginResponseDto;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.dto.response.UserResponseDto;
import com.anlb.readcycle.mapper.UserMapper;
import com.anlb.readcycle.dto.response.LoginResponseDto.UserGetAccount;
import com.anlb.readcycle.dto.response.LoginResponseDto.UserLogin;
import com.anlb.readcycle.repository.UserRepository;
import com.anlb.readcycle.service.IMaintenanceService;
import com.anlb.readcycle.service.IRoleService;
import com.anlb.readcycle.service.IUserLogService;
import com.anlb.readcycle.service.IUserService;
import com.anlb.readcycle.service.criteria.UserCriteria;
import com.anlb.readcycle.service.query.UserQueryService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.anlb.readcycle.utils.exception.RegisterValidator;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final JwtDecoder jwtDecoder;
    private final IRoleService roleService;
    private final UserMapper userMapper;
    private final IUserLogService userLogService;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final IMaintenanceService maintenanceService;

    /**
     * Handles the registration process for a new member.
     *
     * This method generates an email verification token, sets the email verification
     * status to false, assigns the token to the user, and then saves the user to the database.
     *
     * @param user The {@link User} entity to be registered.
     * @return The saved {@link User} entity with the verification token.
     */
    @Override
    public User handleRegisterMember(User user) {
        String verifyEmailToken = securityUtil.createVerifyEmailToken(user.getEmail());
        user.setEmailVerified(false);
        user.setVerificationEmailToken(verifyEmailToken);
        return userRepository.save(user);
    }

    /**
     * Creates a new user and sets initial attributes.
     *
     * This method generates a verification email token, sets the email as unverified,
     * assigns a generated password, and marks the user as active before saving it to the database.
     * Additionally, it logs the user creation activity.
     *
     * @param user the {@link User} object containing user details.
     * @return the saved {@link User} entity with updated attributes.
     * @throws InvalidException if the access token is invalid.
     */
    @Override
    public User handleCreateUser(User user) throws InvalidException {
        String verifyEmailToken = securityUtil.createVerifyEmailToken(user.getEmail());
        user.setEmailVerified(false);
        user.setVerificationEmailToken(verifyEmailToken);
        user.setPassword(generatePassword());
        user.setActive(true);
        user = userRepository.save(user);
        String email = SecurityUtil.getCurrentUserLogin()
                        .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User userLogin = this.handleGetUserByUsername(email);
        userLogService.logCreateUser(user, userLogin);
        return user;
    }

    /**
     * Generates a random password consisting of alphanumeric characters and one special character.
     *
     * The password is generated using a combination of lowercase letters (a-z),
     * numbers (0-9), and a randomly selected special character from a predefined set.
     *
     * @return a randomly generated password as a {@link String}.
     */
    public static String generatePassword() {
        final String LETTERS_AND_NUMBERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int index = (int) (Math.random() * LETTERS_AND_NUMBERS.length());
            password.append(LETTERS_AND_NUMBERS.charAt(index));
        }
        int specialIndex = (int) (Math.random() * SPECIAL_CHARACTERS.length());
        password.append(SPECIAL_CHARACTERS.charAt(specialIndex));
        return password.toString();
    }

    /**
     * Checks if a user exists in the system by their email.
     *
     * @param email The email address to check.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    @Override
    public boolean handleCheckExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Retrieves a user by their username (email).
     *
     * @param username The username (email) of the user.
     * @return The {@code User} object corresponding to the given username.
     *
     */
    @Override
    public User handleGetUserByUsername(String username) throws InvalidException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new InvalidException("Bad credentials");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidException("Your account has not been verified");
        }
        
        return user;
    }

    /**
     * Retrieves a user by their username (email) with additional validation checks.
     *
     * <p>This method fetches a user from the database using their email address.
     * It performs the following validations:</p>
     * <ul>
     *     <li>Throws an exception if the user does not exist.</li>
     *     <li>Throws an exception if the user's email has not been verified.</li>
     *     <li>Throws an exception if the system is in maintenance mode and the user is a regular user.</li>
     * </ul>
     *
     * @param username the email of the user to be retrieved.
     * @return the {@link User} object if found and validated successfully.
     * @throws InvalidException if the user does not exist, the email is not verified,
     *                          or the system is in maintenance mode for regular users.
     */
    @Override
    public User handleGetUserByUsernameV2(String username) throws InvalidException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new InvalidException("Bad credentials");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidException("Your account has not been verified");
        }

        if (maintenanceService.getMaintenance().isMaintenanceMode() && user.getRole().getName().equals("user")) {
            throw new InvalidException("Maintenance mode, we will be back soon");
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
    @Override
    public User handleVerifyEmail(String token) {
        User user = userRepository.findByVerificationEmailToken(token).get();
        user.setEmailVerified(true);
        user.setVerificationEmailToken(null);
        return userRepository.save(user);
    }

    /**
     * Validates a JWT token by decoding it and checking its expiration time.
     * If the token is expired or invalid, the method returns {@code false}.
     *
     * @param token The JWT token to be validated.
     * @return {@code true} if the token is valid and not expired, otherwise {@code false}.
     * @throws JwtException if the token is malformed or cannot be decoded.
     */
    @Override
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
    @Override
    public String extractEmailFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("email");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves a user by their email verification token.
     *
     * This method searches for a user in the database using the provided verification token.
     * It is typically used during the email verification process.
     *
     * @param token the verification token associated with the user.
     * @return the {@link User} object if a match is found, otherwise returns {@code null}.
     */
    @Override
    public User handleFindUserByVerifyToken(String token) {
        return userRepository.findUserByVerificationEmailToken(token);
    }

    /**
     * Deletes a user from the repository based on their email.
     *
     * @param email The email of the user to be deleted.
     */
    @Override
    public void handleDeleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
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
    @Override
    public void handleUpdateRefreshTokenIntoUser(String refreshToken, String email) throws InvalidException {
        User user = handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    /**
     * Validates a refresh token and email against the stored user data.
     *
     * @param refreshToken the refresh token to verify
     * @param decodedToken the decoded JWT containing the user's subject (email or username)
     * @throws InvalidException if no user is found with the provided refresh token and email
     */
    @Override
    public void handleGetUserByRefreshTokenAndEmail(String refreshToken, Jwt decodedToken) throws InvalidException {  
        User dbUser = userRepository.findByRefreshTokenAndEmail(refreshToken, decodedToken.getSubject());
        if (dbUser == null) {
            throw new InvalidException("Refresh token is not valid");
        }
    }

    /**
     * Generates a {@link LoginResponseDto} from a decoded JWT token.
     *
     * @param decodedToken the decoded JWT containing the user's subject (email or username)
     * @return a {@link LoginResponseDto} containing user details and an access token
     * @throws InvalidException if the user associated with the token does not exist
     */
    @Override
    public LoginResponseDto generateLoginResponseFromToken (Jwt decodedToken) throws InvalidException {
        User dbUser = handleGetUserByUsername(decodedToken.getSubject());
        UserLogin user = new UserLogin(
            dbUser.getId(),
            dbUser.getEmail(),
            dbUser.getName(),
            dbUser.getRole()
        );

        LoginResponseDto response = new LoginResponseDto();
        response.setUser(user);

        // set access token
        String accessToken = securityUtil.createAccessToken(decodedToken.getSubject(), response);
        response.setAccessToken(accessToken);
        return response;
    }

    /**
     * Retrieves a paginated list of users based on the provided specification.
     *
     * @param spec     The specification to filter users.
     * @param pageable The pagination and sorting information.
     * @return A {@link ResultPaginateDto} containing the paginated user list and metadata.
     */
    @Override
    public ResultPaginateDto handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(spec, pageable);
        ResultPaginateDto rs = new ResultPaginateDto();
        ResultPaginateDto.Meta mt = new ResultPaginateDto.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<UserResponseDto> listUser = pageUser.getContent()
                                            .stream()
                                            .map(item -> userMapper.convertUserToUserResponseDto(item))
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
    @Override
    public UserGetAccount getCurrentUserAccount() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                        ? SecurityUtil.getCurrentUserLogin().get() : "";
        User dbUser = handleGetUserByUsername(email);
        UserLogin userLogin = userMapper.convertUserToUserLogin(dbUser);
        return convertUserLoginToUserGetAccount(userLogin);
    }

    /**
     * Converts a UserLogin object into a UserGetAccount object.
     * 
     * @param userLogin The UserLogin object containing user details.
     * @return A UserGetAccount object that encapsulates the user information.
     */
    @Override
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
    @Override
    public User handleGetUserById(long id) throws InvalidException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new InvalidException("User with id: " + id + " does not exists");
        }
        return user;
    }

    /**
     * Updates an existing user and logs the update activity.
     *
     * <p>This method retrieves the user by ID, clones their current state, updates their 
     * attributes based on the provided request data, validates the date format, and assigns 
     * the appropriate role. The update action is then logged before saving the changes to the database.
     *
     * @param reqUser the Dto containing updated user information
     * @return the updated and saved {@link User} entity
     * @throws InvalidException if the access token is invalid or the current user cannot be retrieved
     */
    @Override
    public User handleUpdateUser(UpdateUserRequestDto reqUser) throws InvalidException {
        User updateUser = handleGetUserById(reqUser.getId());
        User oldUser = updateUser.clone();
        updateUser.setName(reqUser.getName());
        updateUser.setEmail(reqUser.getEmail());
        if (RegisterValidator.isValidDateFormat(reqUser.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            updateUser.setDateOfBirth(LocalDate.parse(reqUser.getDateOfBirth(), formatter));
        }
        updateUser.setRole(roleService.handleFindByName(reqUser.getRole()));
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User user = handleGetUserByUsername(email);
        userLogService.logUpdateUser(oldUser, updateUser, user);
        return userRepository.save(updateUser);
    }

    /**
     * Deletes a user by their ID and logs the deletion activity.
     *
     * @param id the ID of the user to be deleted
     * @throws InvalidException if the access token is invalid, or the user attempts to delete their own account
     */
    @Override
    public void handleDeleteUserById(long id) throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        User userLogin = handleGetUserByUsername(email);
        User user = handleGetUserById(id);
        if (user.getEmail().equals(email)) {
            throw new InvalidException("You can not delete yourself");
        }
        userLogService.logDeleteUser(id, userLogin);
        userRepository.deleteById(id);
    }

    /**
     * Toggles the active status of a user (soft delete).
     *
     * This method retrieves a user by their ID and toggles their active status.
     * If the user is currently active, they will be deactivated, and vice versa.
     * This provides a soft delete mechanism without permanently removing the user from the database.
     *
     * @param id the unique identifier of the user to be soft deleted.
     * @return the updated {@link User} object after toggling the active status.
     * @throws InvalidException if the user is not found.
     */
	@Override
	public User handleSoftDelete(long id) throws InvalidException {
		User isDeletedUser = handleGetUserById(id);
        isDeletedUser.setActive(!isDeletedUser.isActive());
        return userRepository.save(isDeletedUser);
	}

    /**
     * Changes the password of a user.
     *
     * This method verifies the current password of the user and updates it with a new password.
     * If the provided current password matches the stored password, the new password is hashed and saved.
     * Otherwise, an {@link InvalidException} is thrown.
     *
     * @param changePasswordDto the DTO containing the username, current password, and new password.
     * @throws InvalidException if the current password is incorrect.
     */
    @Override
    public void handleChangePassword(ChangePasswordRequestDto changePasswordDto) throws InvalidException {
        User dbUser = handleGetUserByUsername(changePasswordDto.getUsername());
        if (changePasswordDto.getPassword().equals(dbUser.getPassword())) {
            String hashPassword = this.passwordEncoder.encode(changePasswordDto.getNewPassword());
            dbUser.setPassword(hashPassword);
            userRepository.save(dbUser);
        } else {
            throw new InvalidException("Incorrect password. Please check again");
        }
    }

    /**
     * Retrieves a paginated list of users based on the specified criteria.
     *
     * This method queries users using {@link UserCriteria} and returns the results in a paginated format.
     * It constructs metadata including the current page, page size, total pages, and total elements.
     *
     * @param userCriteria the filtering criteria for retrieving users.
     * @param pageable     the pagination details.
     * @return a {@link ResultPaginateDto} containing the paginated user list and metadata.
     */
    @Override
    public ResultPaginateDto handleGetAllUsers(UserCriteria userCriteria, Pageable pageable) {
        Page<User> pageUser = userQueryService.findByCriteria(userCriteria, pageable);
        ResultPaginateDto response = new ResultPaginateDto();
        ResultPaginateDto.Meta meta = new ResultPaginateDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        response.setMeta(meta);
        List<UserResponseDto> listUser = pageUser.getContent()
                                            .stream()
                                            .map(item -> userMapper.convertUserToUserResponseDto(item))
                                            .collect(Collectors.toList());

        response.setResult(listUser);
        return response;
    }
}
