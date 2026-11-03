package com.xietee.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * BusinessException 的构造与错误码测试。
 * 注意：getCode() 是包级私有方法，所以测试类放在同一个 package 下。
 */
class BusinessExceptionTest {

    @Test
    @DisplayName("构造器(StatusCode)：复制枚举里的 code 和 message")
    void statusCodeConstructorCopiesCodeAndMessage() {
        BusinessException ex = new BusinessException(BusinessExceptionEnum.STUDENT_NOT_FOUND);

        assertEquals(2001, ex.getCode());
        assertEquals("未查找到学生", ex.getMessage());
    }

    @Test
    @DisplayName("构造器(int, String)：支持自定义错误码和提示")
    void codeAndMessageConstructor() {
        BusinessException ex = new BusinessException(4002, "无效参数");

        assertEquals(4002, ex.getCode());
        assertEquals("无效参数", ex.getMessage());
    }

    @Test
    @DisplayName("BusinessException 是 RuntimeException，可被 CLI 统一捕获")
    void isRuntimeException() {
        BusinessException ex = new BusinessException(BusinessExceptionEnum.NO_DATA);

        assertInstanceOf(RuntimeException.class, ex);
    }
}
