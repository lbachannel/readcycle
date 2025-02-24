package com.anlb.readcycle.utils.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookStatusEnum {
    AVAILABLE, UNAVAILABLE;

    /**
     * @JsonCreator: allows automatic handling when receiving data from the client [ex: postman].
     * logic:
     *        if status empty or invalid return default value
     */
    @JsonCreator
    public static BookStatusEnum fromString(String status) {
        if (status == null || status.isEmpty()) {
            return AVAILABLE;
        }
        try {
            return BookStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AVAILABLE;
        }
    }
}
