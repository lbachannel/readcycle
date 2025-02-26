package com.anlb.readcycle.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String username;
    private String password;
    private String newPassword;
    private String confirmNewPassword;
}
