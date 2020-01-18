package com.todo.hulkstore.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceException extends RuntimeException {

    static final long serialVersionUID = -9072699010042657847L;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.ex = cause;
    }

    String message;
    Throwable ex;
}
