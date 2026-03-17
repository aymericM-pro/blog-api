package com.example.blog.enums;

public enum ArticleError implements BusinessError {
    ARTICLE_NOT_FOUND(1001, "Article not found", 404),
    ARTICLE_SLUG_ALREADY_EXISTS(1002, "Article slug already exists", 409),
    ARTICLE_TITLE_ALREADY_EXISTS(1003, "Article title already exists", 409),
    ARTICLE_FORBIDDEN(1004, "You are not allowed to perform this action", 403),
    ARTICLE_INVALID_CATEGORY(1005, "Invalid article category", 400);

    private final int code;
    private final String message;
    private final int httpStatus;

    ArticleError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
