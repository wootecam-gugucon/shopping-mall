package com.gugucon.shopping.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @Value("${stacktrace.limit:1}")
    private Integer stackTraceLimit;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        printCustomStackTrace(e);
        return ErrorResponse.from(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        return ErrorResponse.of(ErrorCode.REQUIRED_FIELD_MISSING, joinFieldErrorMessages(e));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleApplicationException(final ShoppingException e) {
        final ErrorCode errorCode = e.getErrorCode();
        if (errorCode.getStatus().is5xxServerError()) {
            printCustomStackTrace(e);
        }
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    private String joinFieldErrorMessages(final MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
                .map(this::resolveFieldErrorMessage)
                .collect(Collectors.joining(", "));
    }

    private String resolveFieldErrorMessage(final FieldError error) {
        final Object[] arguments = error.getArguments();
        final Locale locale = LocaleContextHolder.getLocale();

        return Arrays.stream(error.getCodes())
                .map(c -> {
                    try {
                        return messageSource.getMessage(c, arguments, locale);
                    } catch (NoSuchMessageException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(error.getDefaultMessage());
    }

    private void printCustomStackTrace(final Exception e) {
        final StackTraceElement[] stackTrace = e.getStackTrace();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("resolved exception : ").append(e.getClass()).append("\n")
                .append("message : ").append(e.getMessage()).append("\n")
                .append("cause : ").append(e.getCause()).append("\n");

        for (int i = 0; i < stackTraceLimit; i++) {
            final String className = stackTrace[i].getClassName();
            final String methodName = stackTrace[i].getMethodName();
            final int lineNumber = stackTrace[i].getLineNumber();
            stringBuilder
                    .append("\n[").append(i).append("]\n")
                    .append("className : ").append(className).append("\n")
                    .append("methodName : ").append(methodName).append("\n")
                    .append("lineNumber : ").append(lineNumber).append("\n");
        }
        log.error(stringBuilder.toString());
    }
}
