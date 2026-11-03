package com.xietee.dto;

import com.xietee.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BookSaveDTO 的校验规则测试。
 */
class BookSaveDTOValidationTest {

    private static BookSaveDTO valid() {
        return new BookSaveDTO("9787111000001", "Java编程思想", "张三", true);
    }

    private static BookSaveDTO withISBN(String ISBN) {
        return new BookSaveDTO(ISBN, "Java编程思想", "张三", true);
    }

    private static BookSaveDTO withTitle(String title) {
        return new BookSaveDTO("9787111000001", title, "张三", true);
    }

    private static BookSaveDTO withAuthor(String author) {
        return new BookSaveDTO("9787111000001", "Java编程思想", author, true);
    }

    private static void assertInvalid(BookSaveDTO dto, String expectedMessage) {
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
    @DisplayName("可借状态为 true / false 都合法")
    void bothAvailabilityStatesPass() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(
                new BookSaveDTO("9787111000001", "Java编程思想", "张三", false)));
    }

    // ---------------------------------------------------------------- ISBN

    @Test
    @DisplayName("ISBN 为空时校验失败")
    void blankISBNFails() {
        assertInvalid(withISBN(""), "ISBN不能为空");
    }

    @Test
    @DisplayName("ISBN 为 12 位时校验失败")
    void twelveDigitISBNFails() {
        assertInvalid(withISBN("978711100000"), "ISBN-13 必须为 13 位数");
    }

    @Test
    @DisplayName("ISBN 为 14 位时校验失败")
    void fourteenDigitISBNFails() {
        assertInvalid(withISBN("97871110000011"), "ISBN-13 必须为 13 位数");
    }

    // ---------------------------------------------------------------- 书名

    @Test
    @DisplayName("书名为空时校验失败")
    void blankTitleFails() {
        assertInvalid(withTitle("  "), "书名不能为空");
    }

    @Test
    @DisplayName("书名超过 255 个字符时校验失败")
    void tooLongTitleFails() {
        assertInvalid(withTitle("a".repeat(256)), "书名长度过长");
    }

    @Test
    @DisplayName("书名恰好 255 个字符时校验通过")
    void maxLengthTitlePasses() {
        assertDoesNotThrow(() -> ValidationUtils.validateOrThrow(withTitle("a".repeat(255))));
    }

    // ---------------------------------------------------------------- 作者

    @Test
    @DisplayName("作者为空时校验失败")
    void blankAuthorFails() {
        assertInvalid(withAuthor(""), "作者名字不能为空");
    }

    @Test
    @DisplayName("作者超过 10 个字符时校验失败")
    void tooLongAuthorFails() {
        assertInvalid(withAuthor("a".repeat(200)), "作者名字过长");
    }
}
