package com.kelp_6.banking_apps.exception;

import com.kelp_6.banking_apps.model.web.WebResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<WebResponse<Object>> constraintViolationException(ConstraintViolationException exception) {
        log.info("[ {} ] {}", HttpStatus.BAD_REQUEST, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("BAD REQUEST")
                .message(exception.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> methodArgumentNotValidException(MethodArgumentNotValidException exception){
        log.info("[ {} ] {}", HttpStatus.BAD_REQUEST, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("BAD REQUEST")
                .message(exception.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<WebResponse<Object>> badCredentialsException(BadCredentialsException exception) {
        log.info("[ {} ] {}", HttpStatus.UNAUTHORIZED, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("UNAUTHORIZED")
                .message("bad credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<WebResponse<Object>> usernameNotFoundException(UsernameNotFoundException exception){
        log.info("[ {} ] {}", HttpStatus.NOT_FOUND, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("NOT FOUND")
                .message("user not found")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<WebResponse<Object>> accessDeniedException(AccessDeniedException exception) {
        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("FORBIDDEN")
                .message("unauthorized user")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { ExpiredJwtException.class })
    public ResponseEntity<WebResponse<Object>> expiredJwtException(ExpiredJwtException exception) {
        log.info("[ {} ] {}", HttpStatus.UNAUTHORIZED, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("UNAUTHORIZED")
                .message("expired credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { SignatureException.class })
    public ResponseEntity<WebResponse<Object>> signatureException(SignatureException exception) {
        log.info("[ {} ] {}", HttpStatus.UNAUTHORIZED, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("invalid credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    public ResponseEntity<WebResponse<Object>> applicationException(ResponseStatusException exception) {
        log.info("[ {} ] {}", exception.getStatusCode(), exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(exception.getStatusCode().toString().substring(4).replace("_", " "))
                .message(exception.getReason())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, exception.getStatusCode());
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<WebResponse<Object>> allException(Exception exception) {
        log.info("[ {} ] {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("INTERNAL SERVER ERROR")
                .message("something wrong")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

