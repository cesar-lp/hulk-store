package com.todo.hulkstore.exception;

import com.todo.hulkstore.exception.error.InvalidProductOrderError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidProductOrderException extends RuntimeException {

    static long serialVersionUID = -2956410343172342374L;

    List<InvalidProductOrderError> invalidProductOrderErrors;
}
