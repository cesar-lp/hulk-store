package com.todo.hulkstore.utils;

import java.math.BigDecimal;

public class ValidationUtils {

    public static void checkNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkNotNullNorEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkNotNullNorNegative(BigDecimal value, String errorMessage) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkNotNullNorNegative(Integer value, String errorMessage) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
