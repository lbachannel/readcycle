package com.anlb.readcycle.domain.dto;

import com.anlb.readcycle.utils.exception.LoginChecked;

import lombok.Data;

@Data
@LoginChecked
public class LoginDTO {
    private String username;
    private String password;
}
