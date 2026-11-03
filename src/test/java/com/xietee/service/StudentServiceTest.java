package com.xietee.service;

import com.xietee.dto.RecordSaveDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Book;
import com.xietee.entity.Record;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;
import com.xietee.support.InMemoryBookMapper;
import com.xietee.support.InMemoryRecordMapper;
import com.xietee.support.InMemoryStudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StudentService 的纯单元测试：学生端个人信息、可借书籍、借阅/归还、历史记录。
 */
class StudentServiceTest {

    private static final String STUDENT_ID = "202400010012";
    private static final String ISBN_JAVA = "9787111000001";
    private static final String ISBN_MYSQL = "9787121000002";

    private InMemoryStudentMapper studentMapper;
    private InMemoryBookMapper bookMapper;
    private InMemoryRecordMapper recordMapper;
    private StudentService service;

    @BeforeEach
    void setUp() {
        studentMapper = new InMemoryStudentMapper();
        bookMapper = new InMemoryBookMapper();
        recordMapper = new InMemoryRecordMapper();
        service = new StudentService(studentMapper, bookMapper, recordMapper);
    }

    private Student seedStudent() {
        return studentMapper.seed(STUDENT_ID, "password1", "张三", "13800000001");
    }

    private Book seedBook(String ISBN, String title, boolean available) {
        return bookMapper.seed(ISBN, title, "张三", available);
    }

    /** 三本 Java（两本可借、一本借出）+ 一本可借的 MySQL。 */
    private void seedCopies() {
        seedBook(ISBN_JAVA, "Java编程思想", true);
        seedBook(ISBN_JAVA, "Java编程思想", true);
        seedBook(ISBN_JAVA, "Java编程思想", false);
        seedBook(ISBN_MYSQL, "MySQL必知必会", true);
    }

    // ------------------------------------------------------------ 个人信息

    @Test
    @DisplayName("updateStudentById：更新密码和手机号")
    void updateStudentByIdChangesPasswordAndPhoneNumber() {
        Student student = seedStudent();
        StudentUpdateDTO dto = new StudentUpdateDTO("newPassword1", "13900000002");

        assertEquals(1, service.updateStudentById(student.getId(), dto));

        Student updated = studentMapper.findById(student.getId()).orElseThrow();
        assertEquals("newPassword1", updated.getPassword());
        assertEquals("13900000002", updated.getPhoneNumber());
    }

    @Test
    @DisplayName("updateStudentById：学生不存在时抛出 UPDATE_FAIL")
    void updateStudentByIdThrowsWhenMissing() {
        StudentUpdateDTO dto = new StudentUpdateDTO("newPassword1", "13900000002");

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.updateStudentById(99L, dto));
        assertEquals("更新失败", ex.getMessage());
    }

    @Test
    @DisplayName("existedStudentID / existedPhoneNumber：存在与不存在")
    void existedChecks() {
        seedStudent();

        assertTrue(service.existedStudentID(STUDENT_ID));
        assertFalse(service.existedStudentID("202499999999"));
        assertTrue(service.existedPhoneNumber("13800000001"));
        assertFalse(service.existedPhoneNumber("13800000099"));
    }

    // ------------------------------------------------------------ 书籍查询

    @Test
    @DisplayName("findBookByISBN：命中 / 缺失")
    void findBookByISBN() {
        seedBook(ISBN_JAVA, "Java编程思想", true);

        assertEquals(1, service.findBookByISBN(ISBN_JAVA).size());

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findBookByISBN("9787999999999"));
        assertEquals("未查找到书籍 ISBN: 9787999999999", ex.getMessage());
    }

    @Test
    @DisplayName("findBookByTitle / findBookByAuthor：命中 / 缺失")
    void findBookByTitleAndAuthor() {
        seedBook(ISBN_JAVA, "Java编程思想", true);

        assertEquals(1, service.findBookByTitle("Java编程思想").size());
        assertEquals(1, service.findBookByAuthor("张三").size());

        assertEquals("未查找到书籍 书名: 不存在",
                assertThrows(BusinessException.class, () -> service.findBookByTitle("不存在")).getMessage());
        assertEquals("未查找到书籍 书籍作者: 不存在",
                assertThrows(BusinessException.class, () -> service.findBookByAuthor("不存在")).getMessage());
    }

    @Test
    @DisplayName("findBookByAvailable：按可借状态筛选")
    void findBookByAvailable() {
        seedBook(ISBN_JAVA, "Java编程思想", true);
        seedBook(ISBN_MYSQL, "MySQL必知必会", false);

        assertEquals(ISBN_JAVA, service.findBookByAvailable(true).getFirst().getISBN());
        assertEquals(ISBN_MYSQL, service.findBookByAvailable(false).getFirst().getISBN());
    }

    @Test
    @DisplayName("findAllBook：空表抛出 NO_DATA")
    void findAllBookThrowsWhenEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findAllBook());
        assertEquals("还没有数据", ex.getMessage());
    }

    // -------------------------------------------------------- 可借书籍查询

    @Test
    @DisplayName("findAllAvailableBook：只返回可借副本")
    void findAllAvailableBookReturnsAvailableCopies() {
        seedCopies();

        List<Book> books = service.findAllAvailableBook();

        assertEquals(3, books.size());
        assertTrue(books.stream().allMatch(Book::isAvailable));
    }

    @Test
    @DisplayName("findAllAvailableBook：没有可借书籍时抛出 AVAILABLE_BOOK_NOT_FOUND")
    void findAllAvailableBookThrowsWhenNone() {
        seedBook(ISBN_JAVA, "Java编程思想", false);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findAllAvailableBook());
        assertEquals("未查找到可借书籍", ex.getMessage());
    }

    @Test
    @DisplayName("findAllAvailableBookWithCount：按 ISBN 汇总且有可借副本")
    void findAllAvailableBookWithCountGroupsByISBN() {
        seedCopies();

        List<Book> books = service.findAllAvailableBookWithCount();

        assertEquals(2, books.size());
        Book java = books.stream()
                .filter(book -> book.getISBN().equals(ISBN_JAVA))
                .findFirst().orElseThrow();
        assertEquals(3, java.getTotalCount());
        assertEquals(2, java.getAvailableCount());
    }

    @Test
    @DisplayName("findAllAvailableBookWithCount：没有可借书籍时抛出 AVAILABLE_BOOK_NOT_FOUND")
    void findAllAvailableBookWithCountThrowsWhenNone() {
        seedBook(ISBN_JAVA, "Java编程思想", false);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findAllAvailableBookWithCount());
        assertEquals("未查找到可借书籍", ex.getMessage());
    }

    @Test
    @DisplayName("fuzzySearchAvailableBook：关键词为空时返回全部可借副本")
    void fuzzySearchAvailableBookWithBlankKeyword() {
        seedCopies();

        assertEquals(3, service.fuzzySearchAvailableBook("").size());
    }

    @Test
    @DisplayName("fuzzySearchAvailableBook：按关键词过滤且只返回可借副本")
    void fuzzySearchAvailableBookFiltersAvailable() {
        seedCopies();

        List<Book> books = service.fuzzySearchAvailableBook("Java");

        assertEquals(2, books.size(), "三本 Java 中有一本已借出");
        assertTrue(books.stream().allMatch(Book::isAvailable));
    }

    @Test
    @DisplayName("fuzzySearchAvailableBook：没有匹配项返回空列表")
    void fuzzySearchAvailableBookReturnsEmpty() {
        seedCopies();

        assertTrue(service.fuzzySearchAvailableBook("不存在").isEmpty());
    }

    @Test
    @DisplayName("fuzzySearchAvailableBookWithCount：按关键词汇总可借副本")
    void fuzzySearchAvailableBookWithCountGroups() {
        seedCopies();

        List<Book> books = service.fuzzySearchAvailableBookWithCount("Java");

        assertEquals(1, books.size());
        assertEquals(3, books.getFirst().getTotalCount());
        assertEquals(2, books.getFirst().getAvailableCount());
    }

    // ---------------------------------------------------------------- 借阅

    @Test
    @DisplayName("borrowByBookId：借阅成功，记录入库且书籍变为不可借")
    void borrowByBookIdSucceeds() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", true);
        RecordSaveDTO dto = new RecordSaveDTO(STUDENT_ID);

        assertEquals(1, service.borrowByBookId(book.getId(), STUDENT_ID, dto));

        Record record = recordMapper.findAll().getFirst();
        assertEquals(book.getId(), record.getBookID());
        assertEquals(STUDENT_ID, record.getStudentID());
        assertEquals(ISBN_JAVA, record.getISBN());
        assertNotNull(record.getBorrowTime());
        assertNull(record.getReturnTime());

        assertFalse(bookMapper.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    @DisplayName("borrowByBookId：书籍不存在时抛出 AVAILABLE_BOOK_NOT_FOUND")
    void borrowByBookIdThrowsWhenBookMissing() {
        RecordSaveDTO dto = new RecordSaveDTO(STUDENT_ID);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.borrowByBookId(99L, STUDENT_ID, dto));
        assertEquals("未查找到可借书籍", ex.getMessage());
    }

    @Test
    @DisplayName("borrowByBookId：书籍已被借出时不允许借阅")
    void borrowByBookIdRejectsUnavailableBook() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", false);
        RecordSaveDTO dto = new RecordSaveDTO(STUDENT_ID);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.borrowByBookId(book.getId(), STUDENT_ID, dto));
        assertEquals("未查找到可借书籍", ex.getMessage());
        assertEquals(0, recordMapper.size());
    }

    @Test
    @DisplayName("borrowByBookId：同一 ISBN 未归还时不允许重复借阅")
    void borrowByBookIdRejectsDuplicateBorrow() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", true);
        recordMapper.seedBorrowed(book.getId(), STUDENT_ID, ISBN_JAVA);
        RecordSaveDTO dto = new RecordSaveDTO(STUDENT_ID);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.borrowByBookId(book.getId(), STUDENT_ID, dto));
        assertEquals("不可重复借阅同一本书籍", ex.getMessage());
        assertEquals(1, recordMapper.size());
    }

    @Test
    @DisplayName("borrowByBookId：已归还过同一 ISBN 后可以再次借阅")
    void borrowByBookIdAllowsBorrowAfterReturn() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", true);
        recordMapper.seed(book.getId(), STUDENT_ID, ISBN_JAVA,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5));
        RecordSaveDTO dto = new RecordSaveDTO(STUDENT_ID);

        assertEquals(1, service.borrowByBookId(book.getId(), STUDENT_ID, dto));
        assertEquals(2, recordMapper.size());
    }

    // ---------------------------------------------------------------- 归还

    @Test
    @DisplayName("returnBookByRecordIDAndStudentID：归还成功，书籍恢复可借并记录归还时间")
    void returnBookByRecordIdSucceeds() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", false);
        Record record = recordMapper.seedBorrowed(book.getId(), STUDENT_ID, ISBN_JAVA);

        service.returnBookByRecordIDAndStudentID(record.getId(), STUDENT_ID);

        assertTrue(bookMapper.findById(book.getId()).orElseThrow().isAvailable());
        assertNotNull(recordMapper.findById(record.getId()).orElseThrow().getReturnTime());
    }

    @Test
    @DisplayName("returnBookByRecordIDAndStudentID：记录不存在时抛出 UNEXISTED_RECORD")
    void returnBookByRecordIdThrowsWhenRecordMissing() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.returnBookByRecordIDAndStudentID(99L, STUDENT_ID));
        assertEquals("该记录不存在", ex.getMessage());
    }

    @Test
    @DisplayName("returnBookByRecordIDAndStudentID：不能归还别人的记录")
    void returnBookByRecordIdRejectsOtherStudent() {
        Book book = seedBook(ISBN_JAVA, "Java编程思想", false);
        Record record = recordMapper.seedBorrowed(book.getId(), "202400019999", ISBN_JAVA);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.returnBookByRecordIDAndStudentID(record.getId(), STUDENT_ID));
        assertEquals("该记录不存在", ex.getMessage());
        assertFalse(bookMapper.findById(book.getId()).orElseThrow().isAvailable());
    }

    @Test
    @DisplayName("returnBookByRecordIDAndStudentID：记录指向的书籍不存在时抛出 SQL_FOREIGN_KEY_ERROR")
    void returnBookByRecordIdThrowsWhenBookMissing() {
        Record record = recordMapper.seedBorrowed(99L, STUDENT_ID, ISBN_JAVA);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.returnBookByRecordIDAndStudentID(record.getId(), STUDENT_ID));
        assertEquals("外键错误", ex.getMessage());
    }

    // ------------------------------------------------------------ 历史记录

    @Test
    @DisplayName("findAllRecordByStudentID：返回该学生全部记录")
    void findAllRecordByStudentIDSucceeds() {
        recordMapper.seedBorrowed(1L, STUDENT_ID, ISBN_JAVA);
        recordMapper.seed(2L, STUDENT_ID, ISBN_MYSQL,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
        recordMapper.seedBorrowed(3L, "202400019999", ISBN_JAVA);

        assertEquals(2, service.findAllRecordByStudentID(STUDENT_ID).size());
    }

    @Test
    @DisplayName("findAllRecordByStudentID：没有记录时抛出 NO_DATA")
    void findAllRecordByStudentIDThrowsWhenEmpty() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findAllRecordByStudentID(STUDENT_ID));
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findUnReturnedRecordByStudentID：只返回该学生未归还的记录")
    void findUnReturnedRecordByStudentIDFilters() {
        recordMapper.seedBorrowed(1L, STUDENT_ID, ISBN_JAVA);
        recordMapper.seed(2L, STUDENT_ID, ISBN_MYSQL,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));

        List<Record> records = service.findUnReturnedRecordByStudentID(STUDENT_ID);

        assertEquals(1, records.size());
        assertEquals(1L, records.getFirst().getBookID());
    }

    @Test
    @DisplayName("findUnReturnedRecordByStudentID：没有未归还记录时抛出 NO_DATA")
    void findUnReturnedRecordByStudentIDThrowsWhenEmpty() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findUnReturnedRecordByStudentID(STUDENT_ID));
        assertEquals("还没有数据", ex.getMessage());
    }
}
