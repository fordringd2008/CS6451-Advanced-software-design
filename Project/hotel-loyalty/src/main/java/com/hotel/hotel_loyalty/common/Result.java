package com.hotel.hotel_loyalty.common;

import lombok.Data;

/**
 * Unified API response wrapper (Alibaba rule: every Controller returns a
 * standard Result object with code, message and data).
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class Result<T> {

    /** Status code: 0 = success, non-zero = business error. */
    private Integer code;

    /** Human-readable message. */
    private String message;

    /** Response payload, null on error. */
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}