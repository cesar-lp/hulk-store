package com.todo.hulkstore.exception;

import com.todo.hulkstore.exception.error.FieldValidationError;
import com.todo.hulkstore.exception.error.InvalidPaymentOrderError;
import com.todo.hulkstore.exception.error.ResourceNotFoundError;
import com.todo.hulkstore.exception.error.ServiceError;
import com.todo.hulkstore.exception.error.ValidationError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
        var error = new ResourceNotFoundError(ex.getMessage(), getRequestUri(request));
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidProductOrderException.class)
    public ResponseEntity<Object> handleInvalidProductLineException(Exception ex, WebRequest request) {
        var invalidProductLines = ((InvalidProductOrderException) ex).getInvalidProductOrderErrors();
        var error = new InvalidPaymentOrderError(invalidProductLines, getRequestUri(request));
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldValidationError::new)
                .collect(toList());
        var error = new ValidationError(fieldErrors, getRequestUri(request));

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(Exception ex, WebRequest request) {
        var error = buildServiceError("Internal Server Error", ex, 500, getRequestUri(request));
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.valueOf(error.getStatusCode()), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        var error = buildServiceError("Internal Server Error", ex, 500, getRequestUri(request));
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.valueOf(error.getStatusCode()), request);
    }

    private ServiceError buildServiceError(String error, Exception ex, Integer statusCode, String uri) {
        return new ServiceError(error, ex.getMessage(), ex.getCause().getLocalizedMessage(), statusCode, uri);
    }

    private String getRequestUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
