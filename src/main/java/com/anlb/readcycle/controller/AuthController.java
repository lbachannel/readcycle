package com.anlb.readcycle.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.domain.dto.LoginDTO;
import com.anlb.readcycle.domain.dto.RegisterDTO;
import com.anlb.readcycle.domain.response.LoginResponse;
import com.anlb.readcycle.service.EmailService;
import com.anlb.readcycle.service.UserService;
import com.anlb.readcycle.utils.SecurityUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
    public ResponseEntity<User> createNewUser(@Valid @RequestBody RegisterDTO registerDTO) {
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        return ResponseEntity.ok().body(response);
    }
}
