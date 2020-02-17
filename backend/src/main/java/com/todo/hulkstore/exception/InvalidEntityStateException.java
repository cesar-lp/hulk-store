package com.todo.hulkstore.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidEntityStateException extends RuntimeException {

    static long serialVersionUID = -7859446519450992908L;

    Map<String, List<String>> errors = new HashMap<>();

    public InvalidEntityStateException(String fieldName, String error) {
        super();
        errors.put(fieldName, singletonList(error));
    }

    public InvalidEntityStateException(Map<String, List<String>> errors) {
        super();
        this.errors.putAll(errors);
    }

    public String getFieldErrorMessage(String fieldName) {
        return errors.get(fieldName).get(0);
    }
}
