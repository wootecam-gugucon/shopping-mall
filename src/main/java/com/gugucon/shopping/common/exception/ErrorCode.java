package com.gugucon.shopping.common.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생헀습니다."),
    REQUIRED_FIELD_MISSING(BAD_REQUEST, "필수 항목이 누락되었습니다."),
    EMAIL_NOT_REGISTERED(BAD_REQUEST, "등록되지 않은 이메일입니다."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "틀린 비밀번호 입니다."),
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "유효하지 않은 비밀번호 형식입니다."),
    LOGIN_REQUESTED(FORBIDDEN, "로그인이 필요합니다."),
    NO_AUTHORIZATION_HEADER(UNAUTHORIZED, "인증 정보가 없습니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "지원하는 토큰 타입이 아닙니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    DUPLICATE_CART_ITEM(BAD_REQUEST, "장바구니에 이미 존재하는 상품입니다."),
    INVALID_QUANTITY(BAD_REQUEST, "장바구니 상품 수량은 1개 이상 1,000개 이하여야 합니다."),
    INVALID_CART_ITEM(BAD_REQUEST, "존재하지 않는 장바구니 상품입니다."),
    INVALID_PRODUCT(BAD_REQUEST, "존재하지 않는 상품입니다."),
    EXCEED_MAX_TOTAL_PRICE(BAD_REQUEST, "주문 가능한 최대 금액을 초과합니다."),
    INVALID_ORDER(BAD_REQUEST, "존재하지 않는 주문입니다."),
    EMPTY_CART(BAD_REQUEST, "장바구니가 비어 있습니다."),
    INVALID_STOCK(BAD_REQUEST, "재고의 값이 올바르지 않습니다."),
    SOLD_OUT(BAD_REQUEST, "품절된 상품입니다.");

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
