package com.geoTrip.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundExistsException(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleExistsException(EntityExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage();
        return new ResponseEntity<>(message, status);
    }
}