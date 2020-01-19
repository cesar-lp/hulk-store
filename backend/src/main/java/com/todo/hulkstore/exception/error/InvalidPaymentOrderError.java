package com.todo.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidPaymentOrderError implements Serializable {

    private static final long serialVersionUID = -3109360529643328910L;

    public InvalidPaymentOrderError(List<InvalidProductOrderError> invalidProductLines, String path) {
        error = "Invalid Payment Order";
        this.statusCode = 500;
        message = "There are invalid product lines";
        this.invalidProductLines = invalidProductLines;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    Integer statusCode;
    String message;
    List<InvalidProductOrderError> invalidProductLines;
    String path;
    LocalDateTime timestamp;
}
