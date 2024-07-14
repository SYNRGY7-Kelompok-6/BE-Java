package com.kelp_6.banking_apps.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ProblemDetail constraintViolationException(ConstraintViolationException exception) {
        log.info("[ {} ] {}", HttpStatus.BAD_REQUEST, exception.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setProperty("description", exception.getCause());

        return problemDetail;
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ProblemDetail badCredentialsException(BadCredentialsException exception) {
        log.info("[ {} ] {}", HttpStatus.UNAUTHORIZED, exception.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "unauthorized");
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ProblemDetail accessDeniedException(AccessDeniedException exception) {
        log.info("[ {} ] {}", HttpStatus.FORBIDDEN, exception.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "forbidden");
    }

    @ExceptionHandler(value = { ExpiredJwtException.class })
    public ProblemDetail expiredJwtException(ExpiredJwtException exception) {
        log.info("[ {} ] {}", HttpStatus.FORBIDDEN, exception.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "expired credentials");
    }

    @ExceptionHandler(value = { SignatureException.class })
    public ProblemDetail signatureException(SignatureException exception) {
        log.info("[ {} ] {}", HttpStatus.FORBIDDEN, exception.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "invalid credentials");
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    public ProblemDetail applicationException(ResponseStatusException exception) {
        log.info("[ {} ] {}", exception.getStatusCode(), exception.getMessage());

        return ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason());
    }

    @ExceptionHandler(value = { Exception.class })
    public ProblemDetail allException(Exception exception) {
        log.info("[ {} ] {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "something wrong");
    }
}

