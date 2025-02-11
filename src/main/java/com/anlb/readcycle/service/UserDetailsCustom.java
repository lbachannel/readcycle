package com.anlb.readcycle.service;

import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.anlb.readcycle.utils.exception.InvalidException;

import lombok.RequiredArgsConstructor;

@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            com.anlb.readcycle.domain.User user = this.userService.handleGetUserByUsername(username);
            return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (InvalidException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

}