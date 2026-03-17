package com.example.blog.enums;

public enum CommonError implements BusinessError {
    INTERNAL_SERVER_ERROR(9001, "Internal server error", 500),
    VALIDATION_ERROR(9002, "Validation error", 400),
    ENTITY_NOT_FOUND(9003, "Entity not found", 404);

    private final int code;
    private final String message;
    private final int httpStatus;

    CommonError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
