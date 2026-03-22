package com.example.caloryxbackend.common.exception.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Error response")
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, List<String> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
