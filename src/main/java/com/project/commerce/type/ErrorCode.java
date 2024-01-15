package com.project.commerce.type;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ALREADY_USE_NAME("이미 사용 중인 이름입니다."),
    NOT_USER_NAME("등록 되지 않는 유저명 입니다."),
    NOT_EQUALS_PASSWORD("비밀번호가 일치하지 않습니다."),

    NOT_EXIST_OWNER_NAME("등록 되지 않은 소유주 입니다."),
    ALREADY_REGISTER_STORE_NAME("이미 등록된 매장명 입니다."),
    NOT_EXIST_STORE_NAME("존재 하지 않는 매장명 입니다."),
    NOT_EXIST_STORE_BY_KEYWORD("조건에 맞는 매장이 없습니다."),

    NOT_EXIST_PRODUCT_NAME("존재 하지 않는 상품명 입니다."),
    ALREADY_REGISTER_PRODUCT_NAME("이미 등록된 상품명 입니다."),
    NOT_EXIST_PRODUCT_BY_KEYWORD("조건에 맞는 상품이 없습니다."),

    BAD_INPUT_OF_AMOUNT_OR_PRIZE("가격이나 수량 입력이 잘못되었습니다."),

    PRODUCT_SOLD_OUT("현재 상품은 품절입니다."),
    MUST_INPUT_AT_LEAST_ONE("수량을 한 개 이상 입력해야합니다."),
    MORE_THAN_NOW_PRODUCT_AMOUNT("현재 수량보다 많이 담을 수 없습니다."),
    NOT_IN_THE_CART("장바구니에 없는 상품 입니다.");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }
}
