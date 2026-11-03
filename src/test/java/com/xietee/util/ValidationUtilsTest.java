package com.xietee.util;

import com.xietee.dto.StudentSaveDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ValidationUtils 的测试：关注“校验失败时如何抛出异常/拼接提示”。
 */
class ValidationUtilsTest {

    @Test
    @DisplayName("合法对象不抛异常")
    void validObjectDoesNotThrow() {
        StudentSaveDTO dto = new StudentSaveDTO("202400010001", "password1", "张三", "13800000001");

        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(dto));
    }

    @Test
    @DisplayName("多个字段违规时，所有提示被拼接进同一个异常")
    void multipleViolationsAreAggregated() {
        // 四个字段都是空串：
        // 学号触发 NotBlank + Size、密码触发 NotBlank + Size、
        // 姓名触发 NotBlank、手机号触发 NotBlank + Size，共 7 条
        StudentSaveDTO dto = new StudentSaveDTO("", "", "", "");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> ValidationUtils.validateOrThrow(dto));
        String message = ex.getMessage();

        assertTrue(message.contains("学生学号不能为空"));
        assertTrue(message.contains("密码不能为空"));
        assertTrue(message.contains("姓名不能为空"));
        assertTrue(message.contains("电话号码不能为空"));
        assertTrue(message.endsWith("\n"), "每条提示占一行，最后也会带换行");
        assertEquals(7, message.split("\n").length);
    }
}
