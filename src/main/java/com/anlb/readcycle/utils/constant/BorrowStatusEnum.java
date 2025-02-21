package com.anlb.readcycle.utils.constant;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

public enum BorrowStatusEnum {
    BORROWED("Borrowed"),

    RETURNED("Returned"), 
    
    LATE("Late"), 
    
    LOST("Lost");

    @Getter
    @Setter
    private String name;

    BorrowStatusEnum(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
