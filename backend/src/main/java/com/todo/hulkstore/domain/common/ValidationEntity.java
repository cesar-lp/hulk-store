package com.todo.hulkstore.domain.common;

import com.todo.hulkstore.exception.InvalidEntityStateException;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.singletonList;

public abstract class ValidationEntity<T> {

    private Validator validator;

    public ValidationEntity() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected void validateEntity() {
        var violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            var errors = new HashMap<String, List<String>>();

            for (var violation : violations) {
                if (errors.containsKey(violation.getPropertyPath().toString())) {
                    errors.get(violation.getPropertyPath().toString()).add(violation.getMessage());
                    continue;
                }
                errors.put(violation.getPropertyPath().toString(), singletonList(violation.getMessage()));
            }

            throw new InvalidEntityStateException(errors);
        }
    }
}
