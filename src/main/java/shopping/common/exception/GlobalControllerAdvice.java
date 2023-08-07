package shopping.common.exception;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
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

@RestControllerAdvice
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    public GlobalControllerAdvice(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException() {
        return ErrorResponse.from(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        return ErrorResponse.from(ErrorCode.REQUIRED_FIELD_MISSING, joinFieldErrorMessages(e));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleApplicationException(ShoppingException e) {
        final ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    private String joinFieldErrorMessages(final MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
            .map(this::resolveFieldErrorMessage)
            .collect(Collectors.joining(", "));
    }

    private String resolveFieldErrorMessage(FieldError error) {
        Object[] arguments = error.getArguments();
        Locale locale = LocaleContextHolder.getLocale();

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
}
