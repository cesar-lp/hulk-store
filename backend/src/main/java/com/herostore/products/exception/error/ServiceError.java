package com.herostore.products.exception.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceError implements Serializable {

    static final long serialVersionUID = -631244046599531569L;

    public ServiceError(String error, String message, String detailMessage, Integer statusCode, String path) {
        this.error = error;
        this.statusCode = statusCode;
        this.message = message;
        this.detailMessage = detailMessage;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    Integer statusCode;
    String message;
    String detailMessage;
    String path;
    LocalDateTime timestamp;
}
