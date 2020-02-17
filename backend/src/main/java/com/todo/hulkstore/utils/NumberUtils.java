package com.todo.hulkstore.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    private static final int SCALE_TWO_DECIMAL_PLACES = 2;

    private NumberUtils() {}

    public static BigDecimal roundToTwoDecimalPlaces(BigDecimal value) {
        if (value == null) return value;
        return value.setScale(SCALE_TWO_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
