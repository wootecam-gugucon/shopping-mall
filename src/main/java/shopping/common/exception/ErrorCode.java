package shopping.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생헀습니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "필수 항목이 누락되었습니다."),
    EMAIL_NOT_REGISTERED(HttpStatus.UNAUTHORIZED, "등록되지 않은 이메일입니다."),
    PASSWORD_NOT_CORRECT(HttpStatus.UNAUTHORIZED, "틀린 비밀번호 입니다."),
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호 형식입니다."),

    NO_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "지원하는 토큰 타입이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    DUPLICATE_CART_ITEM(HttpStatus.BAD_REQUEST, "장바구니에 이미 존재하는 상품입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "장바구니 상품 수량은 1개 이상 1,000개 이하여야 합니다."),
    INVALID_CART_ITEM(HttpStatus.BAD_REQUEST, "존재하지 않는 장바구니 상품입니다."),
    INVALID_PRODUCT(HttpStatus.BAD_REQUEST, "존재하지 않는 상품입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
