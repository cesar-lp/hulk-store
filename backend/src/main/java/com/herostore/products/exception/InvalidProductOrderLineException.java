package com.herostore.products.exception;

import com.herostore.products.exception.error.InvalidProductOrderLineError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidProductOrderLineException extends RuntimeException {

    static long serialVersionUID = -2956410343172342374L;

    List<InvalidProductOrderLineError> invalidProductOrderLineErrors;
}
