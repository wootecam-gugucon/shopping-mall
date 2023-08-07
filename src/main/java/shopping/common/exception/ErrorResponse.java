package shopping.common.exception;

public final class ErrorResponse {

    private ErrorCode errorCode;
    private String message;

    private ErrorResponse() {
    }

    private ErrorResponse(final ErrorCode errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorResponse from(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode, errorCode.getMessage());
    }

    public static ErrorResponse from(final ErrorCode errorCode, final String message) {
        return new ErrorResponse(errorCode, message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
