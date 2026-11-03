package com.xietee.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * LoginException 的构造测试。
 */
class LoginExceptionTest {

    @Test
    @DisplayName("构造器(StatusCode)：复制登录错误码和提示")
    void statusCodeConstructor() {
        LoginException ex = new LoginException(BusinessExceptionEnum.LOGIN_SESSION_EXPIRED);

        assertEquals(6003, ex.getCode());
        assertEquals("登录失效，需重新登录", ex.getMessage());
    }

    @Test
    @DisplayName("构造器(int, String)：支持自定义错误码")
    void codeAndMessageConstructor() {
        LoginException ex = new LoginException(6000, "密码错误");

        assertEquals(6000, ex.getCode());
        assertEquals("密码错误", ex.getMessage());
    }

    @Test
    @DisplayName("LoginException 是 RuntimeException")
    void isRuntimeException() {
        assertInstanceOf(RuntimeException.class,
                new LoginException(BusinessExceptionEnum.LOGIN_SESSION_EXPIRED));
    }
}
