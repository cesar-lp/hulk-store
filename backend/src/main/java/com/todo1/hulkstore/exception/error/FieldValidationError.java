package com.todo1.hulkstore.exception.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.FieldError;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FieldValidationError implements Serializable {

    private static final long serialVersionUID = -5444400532168326078L;

    public FieldValidationError(FieldError err) {
        this.fieldName = err.getField();
        this.error = err.getDefaultMessage();
        this.invalidValue = err.getRejectedValue();
    }

    String fieldName;
    String error;
    Object invalidValue;
}
