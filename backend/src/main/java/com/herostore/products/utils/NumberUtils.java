package com.herostore.products.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtils {

    private static final int SCALE_TWO_DECIMAL_PLACES = 2;

    private NumberUtils() {
    }

    public static BigDecimal roundToTwoDecimalPlaces(BigDecimal value) {
        if (value == null) return value;
        return value.setScale(SCALE_TWO_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public static String toCurrencyFormat(BigDecimal value) {
        return NumberFormat.getCurrencyInstance().format(value);
    }
}
