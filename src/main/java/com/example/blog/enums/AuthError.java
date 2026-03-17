package com.example.blog.enums;

public enum AuthError implements BusinessError {
    INVALID_CREDENTIALS(2001, "Invalid credentials", 401),
    USER_NOT_FOUND(2002, "User not found", 404),
    USER_ALREADY_EXISTS(2003, "User already exists", 409),
    TOKEN_EXPIRED(2004, "Token has expired", 401),
    TOKEN_INVALID(2005, "Token is invalid", 401),
    ACCESS_DENIED(2006, "Access denied", 403);

    private final int code;
    private final String message;
    private final int httpStatus;

    AuthError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
