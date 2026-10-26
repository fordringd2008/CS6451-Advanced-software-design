package com.hotel.hotel_loyalty.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeDisplayFormatterTest {

    @Test
    void format_shouldUseEuropeanDateAndTwentyFourHourTime() {
        LocalDateTime value = LocalDateTime.of(2026, 1, 2, 0, 5);

        assertThat(DateTimeDisplayFormatter.format(value)).isEqualTo("02/01/2026 00:05");
    }

    @Test
    void format_shouldReturnEmptyStringForNull() {
        assertThat(DateTimeDisplayFormatter.format(null)).isEmpty();
    }
}
