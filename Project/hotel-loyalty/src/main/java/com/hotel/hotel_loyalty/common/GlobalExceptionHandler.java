package com.hotel.hotel_loyalty.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception → unified {@link Result} translation (Alibaba rule:
 * Controller layer must catch and convert all exceptions into a standard
 * response; here centralised in one advice bean).
 *
 * @author wukewei
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Bean Validation failure on a @Valid @RequestBody. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("validation failed: {}", msg);
        return Result.fail(400, msg);
    }

    /** Business precondition violated (e.g. product not found, min > total). */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("bad request: {}", e.getMessage());
        return Result.fail(400, e.getMessage());
    }

    /** Conflicting state (e.g. UC6 Extension 6a: delete blocked by active orders). */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleIllegalState(IllegalStateException e) {
        log.warn("conflict: {}", e.getMessage());
        return Result.fail(409, e.getMessage());
    }

    /** Database uniqueness conflict, such as a duplicate generated business key. */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("database uniqueness conflict: {}", e.getMostSpecificCause().getMessage());
        return Result.fail(409, "the submitted product conflicts with existing data");
    }

    /** Access denied (e.g. IDOR defense: path uid != session uid). */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("access denied: {}", e.getMessage());
        return Result.fail(403, e.getMessage());
    }

    /** Fallback for any uncaught exception. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnexpected(Exception e) {
        log.error("unexpected error", e);
        return Result.fail(500, "internal server error");
    }
}