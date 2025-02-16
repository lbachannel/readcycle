package com.anlb.readcycle.dto.activitylog;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

public enum ActivityGroup {
    BOOK("Book"), USER("User");

    @Getter
    @Setter
    private String name;

    ActivityGroup(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
