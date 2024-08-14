package com.example.userleveltracker.controller;

import com.example.userleveltracker.exception.DataProcessingException;
import com.example.userleveltracker.exception.ErrorDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling specific exceptions across the application.
 */
@ControllerAdvice
@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles exceptions to type {@code DataProcessingException}.
     *
     * @param exception The exception to handle.
     * @param request   The web request associated with the exception.
     * @return A {@link ResponseEntity} containing details of the error.
     */
    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<?> dataNotFoundExceptionHandling(Exception exception, WebRequest request) {
        log.error("An error occurred! Message: {}", exception.getMessage());

        return new ResponseEntity<>(new ErrorDetails(new Date(), exception.getMessage(),
                request.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles method argument validation errors.
     *
     * @param ex      The validation exception to handle.
     * @param headers The headers for the response.
     * @param status  The HTTP status for the response.
     * @param request The web request associated with the validation exception.
     * @return A {@link ResponseEntity} containing details of the validation errors.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("An error occurred! Message: {}", errors.toString());

        return new ResponseEntity<>(new ErrorDetails(new Date(), errors.toString(),
                request.getDescription(false)), HttpStatus.BAD_REQUEST);
    }
}
