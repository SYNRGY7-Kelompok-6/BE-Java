package com.kelp_6.banking_apps.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.utils.StringsUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.DecodingException;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<WebResponse<Object>> constraintViolationException(ConstraintViolationException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> methodArgumentNotValidException(MethodArgumentNotValidException exception){
        LOGGER.error("{}", exception.getMessage());

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
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(formattedErrorMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<WebResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        LOGGER.error("{}", exception.getMessage());
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
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(errorMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<WebResponse<Object>> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception){
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("invalid value: " + exception.getValue())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({FileSizeLimitExceededException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<WebResponse<Object>> fileSizeLimitExceededException(FileSizeLimitExceededException exception){
        WebResponse<Object> response = WebResponse.<Object>builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.toString())
                .message("The file size exceeds the maximum allowed limit!")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<WebResponse<Object>> missingServletRequestParameterException(MissingServletRequestParameterException exception){
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> response = WebResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("Required parameter is missing: " + exception.getParameterName())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<WebResponse<Object>> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        LOGGER.error("{}", ex.getMessage());

        WebResponse<Object> response = WebResponse.<Object>builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("Required header is missing: " + ex.getHeaderName())
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<WebResponse<Object>> badCredentialsException(BadCredentialsException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("bad credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { UsernameNotFoundException.class })
    public ResponseEntity<WebResponse<Object>> usernameNotFoundException(UsernameNotFoundException exception){
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message("user not found")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public ResponseEntity<WebResponse<Object>> accessDeniedException(AccessDeniedException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .message("unauthorized user")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { ExpiredJwtException.class })
    public ResponseEntity<WebResponse<Object>> expiredJwtException(ExpiredJwtException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("expired credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { java.security.SignatureException.class })
    public ResponseEntity<WebResponse<Object>> handleJavaSecuritySignatureException(java.security.SignatureException exception) {
        LOGGER.error("Security Signature Exception: {}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("Invalid credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { io.jsonwebtoken.security.SignatureException.class })
    public ResponseEntity<WebResponse<Object>> handleJwtSignatureException(io.jsonwebtoken.security.SignatureException exception) {
        LOGGER.error("JWT Signature Exception: {}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("Invalid credentials")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(io.jsonwebtoken.io.DecodingException.class)
    public ResponseEntity<WebResponse<Object>> decodingException(DecodingException exception){
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> response = WebResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("invalid token")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    public ResponseEntity<WebResponse<Object>> applicationException(ResponseStatusException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(exception.getStatusCode().toString())
                .message(exception.getReason())
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, exception.getStatusCode());
    }

    @ExceptionHandler(value = { NoResourceFoundException.class })
    public ResponseEntity<WebResponse<Object>> resourceNotFoundException(NoResourceFoundException exception) {
        LOGGER.error("{}", exception.getMessage());

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message("resource not found")
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<WebResponse<Object>> allException(Exception exception) {
        // Log the name of the exception class
        String exceptionName = exception.getClass().getName();
        LOGGER.error("Exception Name: {}", exceptionName);
        LOGGER.info("{}", exception.getMessage());

        // Get the cause if it exists
        Throwable cause = exception.getCause();
        String causeMessage = (cause != null) ? cause.getMessage() : exception.getMessage();

        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message(causeMessage)
                .data(null)
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

