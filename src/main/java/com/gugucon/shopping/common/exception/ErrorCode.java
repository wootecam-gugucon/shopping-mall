package com.gugucon.shopping.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생헀습니다."),
    REQUIRED_FIELD_MISSING(BAD_REQUEST, "필수 항목이 누락되었습니다."),
    EMAIL_NOT_REGISTERED(BAD_REQUEST, "등록되지 않은 이메일입니다."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "틀린 비밀번호 입니다."),
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "유효하지 않은 비밀번호 형식입니다."),
    LOGIN_REQUESTED(FORBIDDEN, "로그인이 필요합니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST, "이미 회원가입된 이메일입니다."),
    PASSWORD_CHECK_NOT_SAME(BAD_REQUEST, "비밀번호와 비밀번호 확인이 서로 다릅니다."),
    NO_AUTHORIZATION_HEADER(UNAUTHORIZED, "인증 정보가 없습니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "지원하는 토큰 타입이 아닙니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    DUPLICATE_CART_ITEM(BAD_REQUEST, "장바구니에 이미 존재하는 상품입니다."),
    INVALID_QUANTITY(BAD_REQUEST, "장바구니 상품 수량은 1개 이상 1,000개 이하여야 합니다."),
    INVALID_MONEY(BAD_REQUEST, "금액은 0이상이어야 합니다"),
    INVALID_CART_ITEM(NOT_FOUND, "존재하지 않는 장바구니 상품입니다."),
    INVALID_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다."),
    EXCEED_MAX_TOTAL_PRICE(BAD_REQUEST, "주문 가능한 최대 금액을 초과합니다."),
    INVALID_ORDER(NOT_FOUND, "존재하지 않는 주문입니다."),
    INVALID_ORDER_ITEM(NOT_FOUND, "존재하지 않는 주문 상품입니다."),
    EMPTY_CART(BAD_REQUEST, "장바구니가 비어 있습니다."),
    PAYED_ORDER(BAD_REQUEST, "이미 결제 완료된 주문입니다."),
    INVALID_PAY(NOT_FOUND, "존재하지 않는 결제 정보입니다."),
    PAY_FAILED(INTERNAL_SERVER_ERROR, "결제에 실패했습니다."),
    NOT_PAYED_ORDER(NOT_FOUND, "아직 결제 처리가 완료되지 않은 주문입니다."),
    STOCK_NOT_ENOUGH(BAD_REQUEST, "상품의 재고가 부족합니다."),
    SOLD_OUT(BAD_REQUEST, "품절된 상품입니다."),
    ALREADY_RATED(BAD_REQUEST, "이미 평가를 완료한 주문 상품입니다."),
    INVALID_RATE(NOT_FOUND, "존재하지 않는 주문 평가 정보입니다."),
    POINT_CHARGE_NOT_POSITIVE(BAD_REQUEST, "포인트 충전 값은 0보다 커야 합니다."),
    POINT_NOT_ENOUGH(BAD_REQUEST, "포인트가 부족합니다."),
    INVALID_SORT(BAD_REQUEST, "잘못된 정렬 기준입니다."),
    INVALID_SCORE(BAD_REQUEST, "별점은 0이상 5이하의 정수여야 합니다."),
    EMPTY_INPUT(BAD_REQUEST, "내용을 입력해주세요."),
    INVALID_PAY_TYPE(BAD_REQUEST, "지원하지 않는 결제 형식입니다."),
    INVALID_ORDER_STATUS(BAD_REQUEST, "주문 상태가 올바르지 않습니다."),
    INVALID_BIRTH_DATE(BAD_REQUEST, "생년월일이 올바르지 않습니다.");

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
