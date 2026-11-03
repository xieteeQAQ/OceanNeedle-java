package com.xietee.dto;

import com.xietee.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RecordSaveDTO 的校验测试。
 */
class RecordSaveDTOValidationTest {

    private static void assertInvalid(RecordSaveDTO dto, String expectedMessage) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> ValidationUtils.validateOrThrow(dto));
        assertTrue(
                ex.getMessage().contains(expectedMessage),
                () -> "期望提示包含 [" + expectedMessage + "]，实际为 [" + ex.getMessage() + "]");
    }

    @Test
    @DisplayName("12 位学号可以通过校验")
    void validStudentIDPasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(
                new RecordSaveDTO("202400010012")));
    }

    @Test
    @DisplayName("学号为空时校验失败")
    void blankStudentIDFails() {
        assertInvalid(new RecordSaveDTO(""), "学生学号不能为空");
    }

    @Test
    @DisplayName("学号不是 12 位时校验失败")
    void wrongLengthStudentIDFails() {
        assertInvalid(new RecordSaveDTO("20240001001"), "学生学号必须为 12 位数");
        assertInvalid(new RecordSaveDTO("2024000100123"), "学生学号必须为 12 位数");
    }
}
