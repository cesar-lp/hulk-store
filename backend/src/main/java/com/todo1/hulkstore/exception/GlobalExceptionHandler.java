package com.todo1.hulkstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResourceNotFoundError>
    handleResourceNotFoundException(final Exception ex, final HttpStatus httpStatus) {
        String msg = ex.getMessage();
        return new ResponseEntity<>(new ResourceNotFoundError(msg, "/product-types/5"), HttpStatus.NOT_FOUND);
    }
}
