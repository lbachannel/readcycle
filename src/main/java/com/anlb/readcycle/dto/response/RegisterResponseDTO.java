package com.anlb.readcycle.dto.response;

import lombok.Data;

@Data
public class RegisterResponseDto {
    private long id;
    private String name;
    private String email;
    private String verificationEmailToken;
}
