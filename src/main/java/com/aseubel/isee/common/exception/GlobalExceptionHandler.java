package com.aseubel.isee.common.exception;

import com.aseubel.isee.common.Response;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 捕获所有异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception ex) {
        log.error("Exception occurred: ", ex);
        Response<?> response = Response.<Object>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .info(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 捕获特定异常，例如 IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: ", ex);
        Response<?> response = Response.<Object>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .info(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 捕获认证失败异常，例如自定义的 AuthException
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Response<?>> handleAuthException(AuthException ex) {
        Response<?> response = Response.<Object>builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .info(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
