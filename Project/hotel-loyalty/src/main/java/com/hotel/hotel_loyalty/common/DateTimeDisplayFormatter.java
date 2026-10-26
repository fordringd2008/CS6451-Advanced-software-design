package com.hotel.hotel_loyalty.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formats timestamps for user-facing pages.
 *
 * @author Fordring
 */
public final class DateTimeDisplayFormatter {

    private static final DateTimeFormatter EUROPEAN_DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateTimeDisplayFormatter() {
    }

    public static String format(LocalDateTime value) {
        return value == null ? "" : EUROPEAN_DATE_TIME.format(value);
    }
}
