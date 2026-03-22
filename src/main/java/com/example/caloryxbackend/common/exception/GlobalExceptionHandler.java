package com.example.caloryxbackend.common.exception;

import com.example.caloryxbackend.common.exception.payload.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Upload file is too large",
                        null
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        ex.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        404,
                        "Not Found",
                        ex.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Validation failed",
                        errors
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "Unexpected error occurred",
                        null
                )
        );
    }
}
