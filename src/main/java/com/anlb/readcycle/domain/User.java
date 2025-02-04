package com.anlb.readcycle.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private boolean emailVerified;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String verificationEmailToken;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
