package com.todo.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FieldValidationError implements Serializable {

    private static final long serialVersionUID = -5444400532168326078L;

    public FieldValidationError(FieldError err) {
        this.fieldName = err.getField();
        this.error = err.getDefaultMessage();
        this.invalidValue = err.getRejectedValue();
    }

    public FieldValidationError(ConstraintViolation violation) {
        this.fieldName = violation.getPropertyPath().toString();
        this.error = violation.getMessage();
        this.invalidValue = violation.getInvalidValue();
    }

    String fieldName;
    String error;
    Object invalidValue;
}
