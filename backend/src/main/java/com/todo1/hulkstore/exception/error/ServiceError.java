package com.todo1.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseError {

    public ResponseError(String error, String message, Integer statusCode, String path) {
        this.error = error;
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    String message;
    Integer statusCode;
    String path;
    LocalDateTime timestamp;
}
