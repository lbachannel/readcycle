package com.anlb.readcycle.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.anlb.readcycle.domain.dto.response.LoginResponseDTO;
import com.anlb.readcycle.utils.exception.InvalidException;
import com.nimbusds.jose.util.Base64;

@Service
public class SecurityUtil {

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // verify email
    @Value("${anlb.jwt.verify-email-token-validity-in-seconds}")
    private long verifyEmailExpired;
    public String createVerifyEmailToken(String email) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.verifyEmailExpired, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("email", email)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // access token
    @Value("${anlb.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpired;

    public String createAccessToken(String email, LoginResponseDTO loginResponse) {
        LoginResponseDTO.UserInsideToken userToken = new LoginResponseDTO.UserInsideToken();
        userToken.setId(loginResponse.getUser().getId());
        userToken.setEmail(loginResponse.getUser().getEmail());
        userToken.setName(loginResponse.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpired, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // refresh token
    @Value("${anlb.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpired;

    public String createRefreshToken(String email, LoginResponseDTO loginResponse) {
        LoginResponseDTO.UserInsideToken userToken = new LoginResponseDTO.UserInsideToken();
        userToken.setId(loginResponse.getUser().getId());
        userToken.setEmail(loginResponse.getUser().getEmail());
        userToken.setName(loginResponse.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpired, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Get the login of the current user
     * 
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String string) {
            return string;
        }
        return null;
    }

    // get refresh token from cookies
    @Value("${anlb.jwt.base64-secret}")
    private String jwtKey;

    private SecretKey getSecretKey() {
        byte keyBytes[] = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    // decoder check token is real or fake
    public Jwt checkValidRefreshToken(String token) throws InvalidException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                                            .withSecretKey(getSecretKey())
                                            .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
                                            .build();
        try {
            if ("abc".equals(token)) {
                throw new InvalidException("You do not have refresh token in cookies");
            }
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw new InvalidException("Refresh token error: " + e.getMessage());
        }
    }
}
