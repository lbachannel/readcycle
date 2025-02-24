package com.anlb.readcycle.dto.request;

import com.anlb.readcycle.utils.exception.LoginChecked;

import lombok.Data;

@Data
@LoginChecked
public class LoginRequestDto {
    private String username;
    private String password;
}
