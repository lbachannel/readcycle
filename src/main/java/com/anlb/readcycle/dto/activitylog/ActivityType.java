package com.anlb.readcycle.dto.activitylog;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

public enum ActivityType {
    CREATE_USER("Create user"),
    UPDATE_USER("Update user"),
    DELETE_USER("Delete user"),

    CREATE_BOOK("Create book"),
    UPDATE_BOOK("Update book"),
    DELETE_BOOK("Delete book"),
    SOFT_DELETE_BOOK("Toggle soft delete book");

    @Getter
    @Setter
    private String name;

    ActivityType(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
