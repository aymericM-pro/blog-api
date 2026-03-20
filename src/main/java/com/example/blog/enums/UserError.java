package com.example.blog.enums;

public enum UserError implements BusinessError {

    USER_NOT_FOUND(3001, "User not found", 404),
    USER_FORBIDDEN(3002, "You are not allowed to perform this action", 403);

    private final int code;
    private final String message;
    private final int httpStatus;

    UserError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
