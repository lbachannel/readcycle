package com.anlb.readcycle.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.ChangePasswordRequestDto;
import com.anlb.readcycle.dto.request.UpdateUserRequestDto;
import com.anlb.readcycle.dto.response.LoginResponseDto;
import com.anlb.readcycle.dto.response.LoginResponseDto.UserGetAccount;
import com.anlb.readcycle.dto.response.LoginResponseDto.UserLogin;
import com.anlb.readcycle.dto.response.ResultPaginateDto;
import com.anlb.readcycle.service.criteria.UserCriteria;
import com.anlb.readcycle.utils.exception.InvalidException;

public interface IUserService {
    User handleRegisterMember(User user);
    User handleCreateUser(User user) throws InvalidException;
    boolean handleCheckExistsByEmail(String email);
    User handleGetUserByUsername(String username) throws InvalidException;
    User handleVerifyEmail(String token);
    boolean validateToken(String token);
    String extractEmailFromToken(String token);
    User handleFindUserByVerifyToken(String token);
    void handleDeleteUserByEmail(String email);
    void handleUpdateRefreshTokenIntoUser(String refreshToken, String email) throws InvalidException;
    void handleGetUserByRefreshTokenAndEmail(String refreshToken, Jwt decodedToken) throws InvalidException;
    LoginResponseDto generateLoginResponseFromToken (Jwt decodedToken) throws InvalidException;
    ResultPaginateDto handleGetAllUsers(Specification<User> spec, Pageable pageable);
    UserGetAccount getCurrentUserAccount() throws InvalidException;
    UserGetAccount convertUserLoginToUserGetAccount(UserLogin userLogin);
    User handleGetUserById(long id) throws InvalidException;
    User handleUpdateUser(UpdateUserRequestDto reqUser) throws InvalidException;
    void handleDeleteUserById(long id) throws InvalidException;
    User handleSoftDelete(long id) throws InvalidException;
    void handleChangePassword(ChangePasswordRequestDto changePasswordDto) throws InvalidException;
    ResultPaginateDto handleGetAllUsers(UserCriteria criteria, Pageable pageable);
}
