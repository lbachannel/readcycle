package com.anlb.readcycle.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.anlb.readcycle.dto.response.ResultResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        delegate.commence(request, response, authException);
        response.setContentType("application/json");
        ResultResponseDto<Object> res = new ResultResponseDto<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        // handle case authException.getCause() is null
        String errorMessage = Optional.ofNullable(authException.getCause())
                                .map(Throwable::getMessage)
                                .orElse(authException.getMessage());

        res.setError(errorMessage);
        res.setMessage("Bad credentials");
        mapper.writeValue(response.getWriter(), res);
    }
}