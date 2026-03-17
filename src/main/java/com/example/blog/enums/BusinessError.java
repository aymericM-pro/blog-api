package com.example.blog.enums;

public interface BusinessError {
    int getCode();
    String getMessage();
    int getHttpStatus();
}
