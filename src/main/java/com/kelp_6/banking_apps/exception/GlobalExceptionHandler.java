package com.kelp_6.banking_apps.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.utils.StringsUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;
import java.util.List;
import java.util.stream.Collectors;

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

        // Extract field errors
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(StringsUtil::formatFieldError)
                .collect(Collectors.toList());

        // Join errors into a single message
        String formattedErrorMessage = String.join("; ", errorMessages);

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("BAD REQUEST")
                .message(formattedErrorMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<WebResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String errorMessage = "Invalid input. Please check your data and try again.";

        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType() == Boolean.class) {
                errorMessage = String.format(
                        "Invalid boolean value: '%s'. Please provide 'true' or 'false'.",
                        invalidFormatException.getValue()
                );
            }
        }

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("BAD REQUEST")
                .message(errorMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<WebResponse<Object>> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception){
        log.info("[ {} ] {}", HttpStatus.BAD_REQUEST, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse.builder()
                .status("BAD REQUEST")
                .message("invalid value: " + exception.getValue())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<WebResponse<Object>> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        WebResponse<Object> response = WebResponse.<Object>builder()
                .status("error")
                .message("Required header is missing: " + ex.getHeaderName())
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(value = { UsernameNotFoundException.class })
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
                .status(exception.getStatusCode().toString().substring(4))
                .message(exception.getReason())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, exception.getStatusCode());
    }

    @ExceptionHandler(value = { NoResourceFoundException.class })
    public ResponseEntity<WebResponse<Object>> resourceNotFoundException(NoResourceFoundException exception) {
        log.info("[ {} ] {}", HttpStatus.NOT_FOUND, exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("NOT FOUND")
                .message("resource not found")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<WebResponse<Object>> allException(Exception exception) {
        log.info("[ {} ] {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());

        // Get the cause if it exists
        Throwable cause = exception.getCause();
        String causeMessage = (cause != null) ? cause.getMessage() : exception.getMessage();

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status("INTERNAL SERVER ERROR")
                .message(causeMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

