package com.todo1.hulkstore.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceNotFoundException extends RuntimeException {

    static final long serialVersionUID = -5170530814743091127L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    String message;
}
