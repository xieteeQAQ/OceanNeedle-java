package com.xietee.common;

import com.xietee.exception.BusinessExceptionEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Result 工厂方法测试：保证各分支的 code / message / data 符合约定。
 */
class ResultTest {

    @Test
    @DisplayName("success()：默认 200 / success / data 为 null")
    void successWithoutArgs() {
        Result<String> result = Result.success();

        assertEquals(200, result.code());
        assertEquals("success", result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("success(data)：保留默认提示")
    void successWithData() {
        Result<String> result = Result.success("hello");

        assertEquals(200, result.code());
        assertEquals("success", result.message());
        assertEquals("hello", result.data());
    }

    @Test
    @DisplayName("success(message, data)：使用自定义提示")
    void successWithMessageAndData() {
        Result<String> result = Result.success("查询成功", "hello");

        assertEquals(200, result.code());
        assertEquals("查询成功", result.message());
        assertEquals("hello", result.data());
    }

    @Test
    @DisplayName("success(StatusCode)：使用枚举里的 code 和 message")
    void successWithStatusCode() {
        Result<Void> result = Result.success(BusinessExceptionEnum.SUCCESS);

        assertEquals(BusinessExceptionEnum.SUCCESS.getCode(), result.code());
        assertEquals(BusinessExceptionEnum.SUCCESS.getMessage(), result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("warn(code, message, data)：保留提示数据")
    void warnWithData() {
        Result<String> result = Result.warn(4001, "无效命令", "extra");

        assertEquals(4001, result.code());
        assertEquals("无效命令", result.message());
        assertEquals("extra", result.data());
    }

    @Test
    @DisplayName("warn(StatusCode)：data 为 null")
    void warnWithStatusCode() {
        Result<String> result = Result.warn(BusinessExceptionEnum.COMMAND_ERROR);

        assertEquals(4001, result.code());
        assertEquals("无效命令", result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("error(code, message)：data 为 null")
    void errorWithCodeAndMessage() {
        Result<String> result = Result.error(500, "系统异常");

        assertEquals(500, result.code());
        assertEquals("系统异常", result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("error(StatusCode)：使用枚举里的 code 和 message")
    void errorWithStatusCode() {
        Result<String> result = Result.error(BusinessExceptionEnum.STUDENT_NOT_FOUND);

        assertEquals(2001, result.code());
        assertEquals("未查找到学生", result.message());
        assertNull(result.data());
    }
}
