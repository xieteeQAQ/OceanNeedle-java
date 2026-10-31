package com.xietee.exception;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    private final int code;

    public LoginException(int code, String message) {
        super(message);
        this.code = code;
    }

    public LoginException(StatusCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
