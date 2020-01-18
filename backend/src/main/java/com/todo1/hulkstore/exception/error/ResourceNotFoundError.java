package com.todo1.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceNotFoundError implements Serializable {

    private static final long serialVersionUID = -5907001417033804966L;

    public ResourceNotFoundError(String message, String path) {
        this.error = "Resource Not Found";
        this.statusCode = 404;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    Integer statusCode;
    String message;
    String path;
    LocalDateTime timestamp;
}
