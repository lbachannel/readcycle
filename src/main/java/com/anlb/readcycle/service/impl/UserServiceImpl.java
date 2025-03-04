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
import com.anlb.readcycle.service.IRoleService;
import com.anlb.readcycle.service.IUserLogService;
import com.anlb.readcycle.service.IUserService;
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
     * Creates a new user and logs the creation activity.
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

	@Override
	public User handleSoftDelete(long id) throws InvalidException {
		User isDeletedUser = handleGetUserById(id);
        isDeletedUser.setActive(!isDeletedUser.isActive());
        return userRepository.save(isDeletedUser);
	}

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
}
