package com.anlb.readcycle.dto.response;

import lombok.Data;

@Data
public class ResultResponseDto<T> {
    private int statusCode;
    private String error;

    private Object message;
    private T data;
}
