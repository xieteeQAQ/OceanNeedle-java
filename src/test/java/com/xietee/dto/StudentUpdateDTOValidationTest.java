package com.xietee.dto;

import com.xietee.entity.Student;
import com.xietee.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StudentUpdateDTO 的校验规则测试。
 */
class StudentUpdateDTOValidationTest {

    private static StudentUpdateDTO valid() {
        return new StudentUpdateDTO("password1", "13800000001");
    }

    private static void assertInvalid(StudentUpdateDTO dto, String expectedMessage) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> ValidationUtils.validateOrThrow(dto));
        assertTrue(
                ex.getMessage().contains(expectedMessage),
                () -> "期望提示包含 [" + expectedMessage + "]，实际为 [" + ex.getMessage() + "]");
    }

    @Test
    @DisplayName("合法数据可以通过校验")
    void validDtoPasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(valid()));
    }

    @Test
    @DisplayName("密码为空时校验失败")
    void blankPasswordFails() {
        assertInvalid(new StudentUpdateDTO("", "13800000001"), "密码不能为空");
    }

    @Test
    @DisplayName("密码 7 位时校验失败（至少 8 位）")
    void sevenCharPasswordFails() {
        assertInvalid(new StudentUpdateDTO("1234567", "13800000001"), "密码长度至少为 8");
    }

    @Test
    @DisplayName("密码 17 位时校验失败（最多 16 位）")
    void seventeenCharPasswordFails() {
        assertInvalid(new StudentUpdateDTO("a".repeat(17), "13800000001"), "密码长度至少为 8");
    }

    @Test
    @DisplayName("密码恰好 16 位时校验通过")
    void sixteenCharPasswordPasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(
                new StudentUpdateDTO("a".repeat(16), "13800000001")));
    }

    @Test
    @DisplayName("手机号为空时校验失败")
    void blankPhoneNumberFails() {
        assertInvalid(new StudentUpdateDTO("password1", ""), "电话号码不能为空");
    }

    @Test
    @DisplayName("手机号不为 11 位时校验失败")
    void tooLongPhoneNumberFails() {
        assertInvalid(new StudentUpdateDTO("password1", "1".repeat(21)), "电话号码应为 11 位数");
    }

    @Test
    @DisplayName("equals(Student)：密码与手机号都没变时应判定为未修改")
    void equalsComparesPasswordAndPhoneNumber() {
        Student student = new Student(1L, "202400010001", "password1", "张三", "13800000001");
        StudentUpdateDTO dto = new StudentUpdateDTO("password1", "13800000001");

        assertTrue(dto.equals(student));
    }

    @Test
    @DisplayName("equals(Student)：密码或手机号变化时视为已修改")
    void equalsReturnsFalseWhenAnyFieldChanged() {
        Student student = new Student(1L, "202400010012", "password1", "张三", "13800000001");

        assertFalse(new StudentUpdateDTO("newPassword1", "13800000001").equals(student));
        assertFalse(new StudentUpdateDTO("password1", "13900000002").equals(student));
    }
}
