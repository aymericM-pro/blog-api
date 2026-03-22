package com.example.blog.enums;

public enum ReviewError implements BusinessError {
    REVIEW_NOT_FOUND(4001, "Review not found", 404),
    REVIEW_ALREADY_EXISTS(4002, "You have already reviewed this article", 409),
    REVIEW_FORBIDDEN(4003, "You are not allowed to perform this action", 403);

    private final int code;
    private final String message;
    private final int httpStatus;

    ReviewError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
