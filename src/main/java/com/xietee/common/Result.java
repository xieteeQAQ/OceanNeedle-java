package com.xietee.common;

import com.xietee.exception.StatusCode;

public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(StatusCode statusCode, T data) {
        return new Result<>(statusCode.getCode(), statusCode.getMessage(), data);
    }

    public static <T> Result<T> success(StatusCode statusCode) {
        return success(statusCode, null);
    }

    public static <T> Result<T> warn(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> warn(int code, String message) {
        return warn(code, message, null);
    }

    public static <T> Result<T> warn(StatusCode statusCode) {
        return warn(statusCode.getCode(), statusCode.getMessage(), null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(StatusCode statusCode) {
        return error(statusCode.getCode(), statusCode.getMessage());
    }
}
