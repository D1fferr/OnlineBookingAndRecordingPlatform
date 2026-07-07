package com.platform.booking.recording.AuthService.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionErrorResponse exceptionErrorResponse;


    @ExceptionHandler(ValidationUserException.class)
    public ResponseEntity<ExceptionErrorResponse> handleUsersNotFoundException(ValidationUserException e){
        exceptionErrorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(exceptionErrorResponse, HttpStatus.NOT_FOUND);
    }
}
