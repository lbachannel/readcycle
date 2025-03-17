package com.anlb.readcycle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.anlb.readcycle.domain.Role;
import com.anlb.readcycle.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    Optional<User> findByVerificationEmailToken(String token);
    void deleteByEmail(String email);
    User findByRefreshTokenAndEmail(String token, String email);
    User findUserByVerificationEmailToken(String token);
    Long countByRole(Role role);
}
