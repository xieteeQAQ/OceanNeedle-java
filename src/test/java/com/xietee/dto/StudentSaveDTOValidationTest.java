package com.xietee.dto;

import com.xietee.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StudentSaveDTO 的校验规则测试（基于 Jakarta Bean Validation）。
 */
class StudentSaveDTOValidationTest {

    private static StudentSaveDTO valid() {
        return new StudentSaveDTO("202400010001", "password1", "张三", "13800000001");
    }

    private static StudentSaveDTO withStudentID(String studentID) {
        return new StudentSaveDTO(studentID, "password1", "张三", "13800000001");
    }

    private static StudentSaveDTO withPassword(String password) {
        return new StudentSaveDTO("202400010001", password, "张三", "13800000001");
    }

    private static StudentSaveDTO withName(String name) {
        return new StudentSaveDTO("202400010001", "password1", name, "13800000001");
    }

    private static StudentSaveDTO withPhoneNumber(String phoneNumber) {
        return new StudentSaveDTO("202400010001", "password1", "张三", phoneNumber);
    }

    private static void assertInvalid(StudentSaveDTO dto, String expectedMessage) {
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

    // ---------------------------------------------------------------- 学号

    @Test
    @DisplayName("学号为空时校验失败")
    void blankStudentIDFails() {
        assertInvalid(withStudentID(""), "学生学号不能为空");
    }

    @Test
    @DisplayName("学号为 11 位时校验失败（要求 12 位）")
    void elevenDigitStudentIDFails() {
        assertInvalid(withStudentID("20240001000"), "学生学号必须为 12 位数");
    }

    @Test
    @DisplayName("学号为 13 位时校验失败（要求 12 位）")
    void thirteenDigitStudentIDFails() {
        assertInvalid(withStudentID("2024000100012"), "学生学号必须为 12 位数");
    }

    // ---------------------------------------------------------------- 密码

    @Test
    @DisplayName("密码为空时校验失败")
    void blankPasswordFails() {
        assertInvalid(withPassword(""), "密码不能为空");
    }

    @Test
    @DisplayName("密码 7 位时校验失败（至少 8 位）")
    void sevenCharPasswordFails() {
        assertInvalid(withPassword("1234567"), "密码长度至少为 8");
    }

    @Test
    @DisplayName("密码 8 位时校验通过")
    void eightCharPasswordPasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(withPassword("12345678")));
    }

    @Test
    @DisplayName("密码 17 位时校验失败（最多 16 位）")
    void seventeenCharPasswordFails() {
        assertInvalid(withPassword("a".repeat(17)), "密码长度至少为 8");
    }

    @Test
    @DisplayName("密码恰好 16 位时校验通过")
    void sixteenCharPasswordPasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(withPassword("a".repeat(16))));
    }

    // ---------------------------------------------------------------- 姓名

    @Test
    @DisplayName("姓名为空时校验失败")
    void blankNameFails() {
        assertInvalid(withName("  "), "姓名不能为空");
    }

    @Test
    @DisplayName("姓名超过 10 个字符时校验失败")
    void tooLongNameFails() {
        assertInvalid(withName("abcdefghijk"), "姓名长度不能超过 10");
    }

    @Test
    @DisplayName("姓名恰好 10 个字符时校验通过")
    void tenCharNamePasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(withName("abcdefghij")));
    }

    // ---------------------------------------------------------------- 手机号

    @Test
    @DisplayName("手机号为空时校验失败")
    void blankPhoneNumberFails() {
        assertInvalid(withPhoneNumber(""), "电话号码不能为空");
    }

    @Test
    @DisplayName("手机号为 10 位时校验失败（要求 11 位）")
    void tenDigitPhoneNumberFails() {
        assertInvalid(withPhoneNumber("1380000000"), "电话号码应为 11 位数");
    }

    @Test
    @DisplayName("手机号为 12 位时校验失败（要求 11 位）")
    void twelveDigitPhoneNumberFails() {
        assertInvalid(withPhoneNumber("138000000011"), "电话号码应为 11 位数");
    }

    @Test
    @DisplayName("字段为 null 时按“不能为空”处理")
    void nullFieldsFail() {
        assertInvalid(new StudentSaveDTO(null, null, null, null), "学生学号不能为空");
    }
}
