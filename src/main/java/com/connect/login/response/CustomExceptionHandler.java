package com.connect.login.response;

import com.connect.login.response.IncorrectPinException;
import com.connect.login.response.UserNotFoundException;
import com.connect.login.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(IncorrectPinException.class)
    public ResponseEntity<ApiResponse<?>> handleIncorrectPinException(IncorrectPinException ex) {
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage());
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        ApiResponse<?> response = new ApiResponse<>(false, "An error occurred: " + ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
