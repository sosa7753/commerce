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
    NOT_EXIST_STORE_BY_KEYWORD("조건에 맞는 매장이 없습니다.");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }
}
