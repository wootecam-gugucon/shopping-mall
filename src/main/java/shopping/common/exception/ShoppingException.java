package shopping.common.exception;

public class ShoppingException extends RuntimeException {

    private final ErrorCode errorCode;

    public ShoppingException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
