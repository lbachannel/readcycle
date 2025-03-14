package com.anlb.readcycle.utils.exception;

public class MaintenanceException extends RuntimeException {
    public MaintenanceException() {
    }

    public MaintenanceException(String message) {
        super(message);
    }

    public MaintenanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaintenanceException(Throwable cause) {
        super(cause);
    }

    public MaintenanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
