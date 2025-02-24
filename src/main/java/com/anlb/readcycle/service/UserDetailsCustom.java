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

    /**
     * Loads a user by their username and returns a {@link UserDetails} object for authentication.
     *
     * @param username The username (or email) of the user to be retrieved.
     * @return A {@link UserDetails} object containing the user's credentials and roles.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            com.anlb.readcycle.domain.User user = userService.handleGetUserByUsername(username);
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