package com.gugucon.shopping.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {
        exception.printStackTrace();
        return ErrorResponse.from(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException exception) {
        return ErrorResponse.of(ErrorCode.REQUIRED_FIELD_MISSING, joinFieldErrorMessages(exception));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MissingServletRequestParameterException exception) {
        return ErrorResponse.from(ErrorCode.EMPTY_INPUT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleApplicationException(final ShoppingException exception) {
        final ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    private String joinFieldErrorMessages(final MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(this::resolveFieldErrorMessage)
                .collect(Collectors.joining(", "));
    }

    private String resolveFieldErrorMessage(final FieldError error) {
        final Object[] arguments = error.getArguments();
        final Locale locale = LocaleContextHolder.getLocale();

        return Arrays.stream(error.getCodes())
                .map(code -> {
                    try {
                        return messageSource.getMessage(code, arguments, locale);
                    } catch (final NoSuchMessageException exception) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(error.getDefaultMessage());
    }
}
