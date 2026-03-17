package com.example.blog.exception;

import com.example.blog.enums.BusinessError;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessError error;

    public BusinessException(BusinessError error) {
        super(error.getMessage());
        this.error = error;
    }

    public BusinessException(BusinessError error, String detail) {
        super(detail);
        this.error = error;
    }
}
