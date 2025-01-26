package com.anlb.readcycle.domain.dto.response;

import lombok.Data;

@Data
public class RegisterResponseDTO {
    private long id;
    private String name;
    private String email;
    private String verificationEmailToken;
}
