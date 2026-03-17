package com.example.blog.enums;

public enum AuthorError implements BusinessError {

    AUTHOR_NOT_FOUND(2001, "Author not found", 404),
    AUTHOR_ALREADY_EXISTS(2002, "Author already exists", 409),
    AUTHOR_FORBIDDEN(2003, "You are not allowed to perform this action", 403),
    AUTHOR_INVALID_DATA(2004, "Invalid author data", 400);

    private final int code;
    private final String message;
    private final int httpStatus;

    AuthorError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
