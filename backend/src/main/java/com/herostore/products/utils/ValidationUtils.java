package com.herostore.products.utils;

import java.math.BigDecimal;

public class ValidationUtils {

    private ValidationUtils() {
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
