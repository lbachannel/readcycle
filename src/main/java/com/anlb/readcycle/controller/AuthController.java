package com.anlb.readcycle.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.dto.request.LoginRequestDto;
import com.anlb.readcycle.dto.response.LoginResponseDto;
import com.anlb.readcycle.mapper.UserMapper;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final UserMapper userMapper;

    @Value("${anlb.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpired;

    /**
     * {@code GET  /auth/verify-email} : Verifies a user's email using the provided token.
     *
     * @param token The email verification token.
     * @return A {@link ResponseEntity} with a redirect to either the success or failure page.
     */
    @GetMapping("/auth/verify-email")
    @ApiMessage("Verify email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        if (!this.userService.validateToken(token)) {
            String email = this.userService.extractEmailFromToken(token);
            this.userService.handleDeleteUserByEmail(email);
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/verify-email-failed"))
                .build();
        }
        
        this.userService.handleVerifyEmail(token);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create("http://localhost:3000/verify-email-success"))
            .build();
    }

    /**
     * {@code POST  /auth/login} : login.
     *
     * @param loginDTO The login request containing username and password.
     * @return A {@link ResponseEntity} containing a {@link LoginResponseDto} 
     *         with user details and a refresh token cookie.
     * @throws InvalidException If authentication fails or the access token is invalid.
     */
    @PostMapping("/auth/login")
    @ApiMessage("Login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginDTO) throws InvalidException {
        User dbUser = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponseDto response = this.userMapper.convertUserToLoginResponseDTO(dbUser, authentication);
        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), response);
        // save refresh token into user
        String email = SecurityUtil.getCurrentUserLogin()
                            .orElseThrow(() -> new InvalidException("Access Token invalid"));
        this.userService.handleUpdateRefreshTokenIntoUser(refreshToken, email);

        /**
         * set cookies
         * .httpOnly(true): only server can use
         * .secure(true): cookies only use with https. for localhost it has no effect
         * .path("/"): allow all api using cookies
         * .maxAge(60): cookies expiration time. defaullt is session 
         * .domain(): my website url
         */
        ResponseCookie responseCookie = ResponseCookie
                                                .from("refresh_token", refreshToken)
                                                .httpOnly(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpired)
                                                .build();
        return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(response);

    }

    /**
     * {@code GET  /auth/account} : get the current user.
     *
     * @return the current user.
     * @throws InvalidException If the user is not authenticated or the account cannot be retrieved.
     */
    @GetMapping("/auth/account")
    @ApiMessage("Get current user login")
    public ResponseEntity<LoginResponseDto.UserGetAccount> getAccount() throws InvalidException {
        return ResponseEntity.ok().body(this.userService.getCurrentUserAccount());
    }

    /**
     * {@code GET  /auth/refresh} : Refreshes the authentication token.
     *
     * @param refreshTK The refresh token extracted from the "refresh_token" cookie.
     *              Defaults to "abc" if the cookie is missing.
     * @return A {@link ResponseEntity} containing the new authentication tokens
     *              in a {@link LoginResponseDto} along with a new refresh token cookie.
     * @throws InvalidException If the refresh token is invalid or the user cannot be authenticated.
     */
    @GetMapping("/auth/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<LoginResponseDto> getRefreshToken(@CookieValue(name = "refresh_token", defaultValue = "abc") String refreshTK) throws InvalidException {
        // decode check token is real or fake
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshTK);

        // check user by token and email
        this.userService.handleGetUserByRefreshTokenAndEmail(refreshTK, decodedToken);

        // issue new token/set refresh token as cookies
        LoginResponseDto response = this.userService.generateLoginResponseFromToken(decodedToken);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(decodedToken.getSubject(), response);

        String email = decodedToken.getSubject();
        // update user
        this.userService.handleUpdateRefreshTokenIntoUser(new_refresh_token, email);
        // set cookies
        ResponseCookie responseCookie = ResponseCookie
                                                .from("refresh_token", new_refresh_token)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(refreshTokenExpired)
                                                .build();

        return ResponseEntity
                        .ok()
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .body(response);
    }

    /**
     * {@code POST  /auth/logout} : Logout.
     *
     * @return A {@link ResponseEntity} with an empty body and a response header
     *                to delete the refresh token cookie.
     * @throws InvalidException If the user is not authenticated or the access token is invalid.
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new InvalidException("Access Token invalid"));
        // update refresh token = null
        this.userService.handleUpdateRefreshTokenIntoUser(null, email);

        // remove refresh token cookie
        ResponseCookie deletResponseCookie = ResponseCookie
                        .from("refresh_token", null)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .build();
        return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, deletResponseCookie.toString())
                    .body(null);

    }
}
