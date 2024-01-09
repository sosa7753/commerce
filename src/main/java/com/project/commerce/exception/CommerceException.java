package com.project.commerce.exception;

import com.project.commerce.type.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommerceException extends RuntimeException {
    private ErrorCode errorCode;

    public CommerceException(ErrorCode errorCode) {
        super("Commerce Exception " + errorCode.getDescription());
        this.errorCode = errorCode;
    }
}