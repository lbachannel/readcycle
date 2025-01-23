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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.request.LoginRequestDTO;
import com.anlb.readcycle.domain.dto.request.RegisterRequestDTO;
import com.anlb.readcycle.domain.dto.response.LoginResponseDTO;
import com.anlb.readcycle.domain.dto.response.LoginResponseDTO.UserGetAccount;
import com.anlb.readcycle.domain.dto.response.LoginResponseDTO.UserLogin;
import com.anlb.readcycle.service.EmailService;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.SecurityUtil;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${anlb.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpired;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, 
                        SecurityUtil securityUtil,
                        UserService userService,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register account")
    public ResponseEntity<User> createNewUser(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        // hash password
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
        registerDTO.setPassword(hashPassword);
        // convert DTO -> User
        User newUser = this.userService.registerDTOtoUser(registerDTO);
        // save user
        newUser = this.userService.handleCreateUser(newUser);
        // send email
        this.emailService.sendEmailFromTemplateSync(newUser, "ReadCycle - Verify your email", "verify-email");
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/auth/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        if (!this.userService.validateToken(token)) {
            String email = this.userService.extractEmailFromToken(token);
            this.userService.handleDeleteUserByEmail(email);
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("http://127.0.0.1:5500/verify-email-failed.html"))
                .build();
        }
        
        this.userService.handleVerifyEmail(token);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create("http://127.0.0.1:5500/verify-email-success.html"))
            .build();
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) throws InvalidException {
        User dbUser = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (dbUser == null) {
            throw new InvalidException("Bad credentials");
        }

        if (!dbUser.isEmailVerified()) {
            throw new InvalidException("Your account has not been verified");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponseDTO response = new LoginResponseDTO();
        // response user info
        UserLogin user = response.new UserLogin();
        user.setId(dbUser.getId());
        user.setEmail(dbUser.getEmail());
        user.setName(dbUser.getName());
        response.setUser(user);

        // create access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), response.getUser());
        // response access-token
        response.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), response);
        // save refresh token into user
        this.userService.handleUpdateRefreshTokenIntoUser(refreshToken, loginDTO.getUsername());

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

    // get account (f5 - refresh page)
    @GetMapping("/auth/account")
    @ApiMessage("Get current user login")
    public ResponseEntity<LoginResponseDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                       ? SecurityUtil.getCurrentUserLogin().get() : "";
        User dbUser = this.userService.handleGetUserByUsername(email);
        LoginResponseDTO response = new LoginResponseDTO();
        UserLogin userLogin = response.new UserLogin();
        UserGetAccount userGetAccount = new UserGetAccount();
        if (dbUser != null) {
            userLogin.setId(dbUser.getId());
            userLogin.setEmail(dbUser.getEmail());
            userLogin.setName(dbUser.getName());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    // get refresh token in db
    @GetMapping("/auth/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<LoginResponseDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token) throws InvalidException {
        // decode check token is real or fake
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token and email
        User dbUser = this.userService.handleGetUserByRefreshTokenAndEmail(refresh_token, email);
        if (dbUser == null) {
            throw new InvalidException("Refresh token is not valid");
        }

        // issue new token/set refresh token as cookies
        LoginResponseDTO response = new LoginResponseDTO();
        User dbUser2 = this.userService.handleGetUserByUsername(email);
        if(dbUser2 != null) {
            LoginResponseDTO.UserLogin userLogin = response.new UserLogin(dbUser2.getId(), dbUser2.getEmail(), dbUser2.getName());
            response.setUser(userLogin);
        }
        UserLogin userLogin = response.new UserLogin();

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, userLogin);
        response.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, response);

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

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                        ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new InvalidException("Access Token invalid");
        }

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
