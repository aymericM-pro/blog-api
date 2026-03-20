package com.example.blog.exception;

import com.example.blog.enums.CommonError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        log.warn("[{}] {} {} → {}", ex.getError().getHttpStatus(), request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse body = ErrorResponse.of(
                ex.getError().getCode(),
                ex.getMessage(),
                ex.getError().getHttpStatus(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getError().getHttpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.warn("[400] {} {} → validation errors: {}", request.getMethod(), request.getRequestURI(), errors);

        ErrorResponse body = ErrorResponse.of(
                CommonError.VALIDATION_ERROR.getCode(),
                CommonError.VALIDATION_ERROR.getMessage(),
                400,
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("[403] {} {} → Access denied", request.getMethod(), request.getRequestURI());

        ErrorResponse body = ErrorResponse.of(2006, "Access denied", 403, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("[401] {} {} → Invalid credentials", request.getMethod(), request.getRequestURI());

        ErrorResponse body = ErrorResponse.of(2001, "Invalid credentials", 401, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        log.warn("[413] {} {} → file too large", request.getMethod(), request.getRequestURI());
        ErrorResponse body = ErrorResponse.of(5003, "File exceeds maximum allowed size (5 MB)", 413, request.getRequestURI());
        return ResponseEntity.status(413).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("[500] {} {} → {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse body = ErrorResponse.of(
                CommonError.INTERNAL_SERVER_ERROR.getCode(),
                CommonError.INTERNAL_SERVER_ERROR.getMessage(),
                500,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
