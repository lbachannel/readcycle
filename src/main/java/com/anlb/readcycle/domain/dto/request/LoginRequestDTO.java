package com.anlb.readcycle.domain.dto.request;

import com.anlb.readcycle.utils.exception.LoginChecked;

import lombok.Data;

@Data
@LoginChecked
public class LoginRequestDTO {
    private String username;
    private String password;
}
