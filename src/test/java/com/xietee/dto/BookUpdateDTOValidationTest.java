package com.xietee.dto;

import com.xietee.entity.Book;
import com.xietee.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BookUpdateDTO 的校验规则与 equals(Book) 测试。
 *
 * <p>管理员只能更新书名和作者；书籍的可借状态由借阅/归还流程维护。</p>
 */
class BookUpdateDTOValidationTest {

    private static BookUpdateDTO valid() {
        return new BookUpdateDTO("Java编程思想", "张三");
    }

    private static void assertInvalid(BookUpdateDTO dto, String expectedMessage) {
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
    @DisplayName("书名为空时校验失败")
    void blankTitleFails() {
        assertInvalid(new BookUpdateDTO("", "张三"), "书名不能为空");
    }

    @Test
    @DisplayName("书名超过 255 个字符时校验失败")
    void tooLongTitleFails() {
        assertInvalid(new BookUpdateDTO("a".repeat(256), "张三"), "书名长度过长");
    }

    @Test
    @DisplayName("作者为空时校验失败")
    void blankAuthorFails() {
        assertInvalid(new BookUpdateDTO("Java编程思想", ""), "作者名字不能为空");
    }

    @Test
    @DisplayName("作者超过 100 个字符时校验失败")
    void tooLongAuthorFails() {
        assertInvalid(new BookUpdateDTO("Java编程思想", "a".repeat(101)), "作者名字过长");
    }

    @Test
    @DisplayName("equals(Book)：书名和作者都没变时视为未修改，与可借状态无关")
    void equalsComparesTitleAndAuthorOnly() {
        Book borrowedBook = new Book(1L, "9787111000001", "Java编程思想", "张三", false, null, null);
        BookUpdateDTO dto = new BookUpdateDTO("Java编程思想", "张三");

        assertTrue(dto.equals(borrowedBook));
    }

    @Test
    @DisplayName("equals(Book)：书名或作者变化时视为已修改")
    void equalsReturnsFalseWhenTitleOrAuthorChanged() {
        Book book = new Book(1L, "9787111000001", "Java编程思想", "张三", true, null, null);

        assertFalse(new BookUpdateDTO("Effective Java", "张三").equals(book));
        assertFalse(new BookUpdateDTO("Java编程思想", "李四").equals(book));
    }
}
