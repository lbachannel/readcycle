package com.anlb.readcycle.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

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

    @Value("${anlb.jwt.token-validity-in-seconds}")
    private long jwtExpired;

    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtExpired, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("anlb", authentication)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
