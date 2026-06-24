package com.visitor_x.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;

    public ApiErrorResponse(int status, String error, String message, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

}
