package com.todo1.hulkstore.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceNotFoundError {

    public ResourceNotFoundError(String message, String path) {
        this.error = "Resource Not Found";
        this.message = message;
        this.statusCode = 404;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    String message;
    Integer statusCode;
    String path;
    LocalDateTime timestamp;
}
