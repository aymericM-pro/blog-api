package com.example.blog.enums;

public enum StorageError implements BusinessError {

    EMPTY_FILE(5001, "File must not be empty", 400),
    INVALID_FILE_TYPE(5002, "File type not allowed. Accepted: jpeg, png, webp, gif, md", 415),
    FILE_TOO_LARGE(5003, "File exceeds maximum allowed size (5 MB)", 413),
    UPLOAD_FAILED(5004, "Failed to upload file to storage", 502),
    DELETE_FAILED(5005, "Failed to delete file from storage", 502),
    INVALID_FILE_URL(5006, "URL does not belong to this bucket", 400);

    private final int code;
    private final String message;
    private final int httpStatus;

    StorageError(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public int getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getHttpStatus() { return httpStatus; }
}
