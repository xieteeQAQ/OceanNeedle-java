package com.xietee.exception;

public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(StatusCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    int getCode() {
        return code;
    }
}
