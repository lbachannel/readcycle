package com.anlb.readcycle.utils.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BorrowStatusEnum {
    BORROWED, RETURNED, LATE, LOST;

    /**
     * @JsonCreator: allows automatic handling when receiving data from the client [ex: postman].
     * logic:
     *        if status empty or invalid return default value
     */
    @JsonCreator
    public static BorrowStatusEnum fromString(String status) {
        if (status == null || status.isEmpty()) {
            return BORROWED;
        }
        try {
            return BorrowStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BORROWED;
        }
    }
}
