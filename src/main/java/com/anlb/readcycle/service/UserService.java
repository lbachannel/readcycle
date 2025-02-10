package com.anlb.readcycle.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    // mapper DTO -> User
    public User registerDTOtoUser(RegisterRequestDTO registerDTO) {
        User user = new User();
        user.setName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        if (RegisterValidator.isValidDateFormat(registerDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(registerDTO.getDateOfBirth(), formatter));
        }
        user.setRole(this.roleService.handleFindById(2).get());

        return user;
    }

    public User convertCreateUserRequestDTOToUser(CreateUserRequestDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        if (RegisterValidator.isValidDateFormat(userDTO.getDateOfBirth())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
        }
        user.setRole(this.roleService.handleFindByName(userDTO.getRole()));
        return user;
    }

    // convert User To RegisterResponseDTO
    public RegisterResponseDTO convertUserToRegisterResponseDTO(User user) {
        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setVerificationEmailToken(user.getVerificationEmailToken());
        return response;
    }

    // create a user response
    public CreateUserResponseDTO convertUserToCreateResponseDTO(User user) {
        CreateUserResponseDTO response = new CreateUserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRole(user.getRole());
        return response;
    }

    public User handleRegisterMember(User user) {
        String verifyEmailToken = this.securityUtil.createVerifyEmailToken(user.getEmail());
        user.setEmailVerified(false);
        user.setVerificationEmailToken(verifyEmailToken);
        return this.userRepository.save(user);
    }
    public User handleCreateUser(User user) {
        user.setEmailVerified(true);
        return this.userRepository.save(user);
    }

    public boolean handleCheckExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public User handleVerifyEmail(String token) {
        User user = this.userRepository.findByVerificationEmailToken(token).get();
        user.setEmailVerified(true);
        user.setVerificationEmailToken(null);
        return this.userRepository.save(user);
    }

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

    public String extractEmailFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("email");
        } catch (Exception e) {
            return null;
        }
    }

    // delete user [email]
    public void handleDeleteUserByEmail(String email) {
        this.userRepository.deleteByEmail(email);
    }

    // save refresh token into user
    public void handleUpdateRefreshTokenIntoUser(String refreshToken, String email) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    // get user [refresh token & email]
    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    // get all users
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
