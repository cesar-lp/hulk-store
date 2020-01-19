package com.todo.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationError implements Serializable {

    private static final long serialVersionUID = 7092895291198451267L;

    public ValidationError(List<FieldValidationError> fieldValidationErrors, String path) {
        this.error = "Validation Error";
        this.statusCode = 422;
        this.fieldValidationErrors = fieldValidationErrors;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    String error;
    Integer statusCode;
    List<FieldValidationError> fieldValidationErrors;
    String path;
    LocalDateTime timestamp;
}
