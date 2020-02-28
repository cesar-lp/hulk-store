package com.herostore.products.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {
    }

    public static String toLocalizedDateTime(LocalDateTime dateTime) {
        var dayMonthYearTimePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(dayMonthYearTimePattern);
    }
}
