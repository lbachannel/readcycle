package com.anlb.readcycle.domain.response;

import lombok.Data;

@Data
public class ResultResponse<T> {
    private int statusCode;
    private String error;

    private Object message;
    private T data;
}
