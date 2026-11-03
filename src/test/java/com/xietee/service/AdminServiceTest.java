package com.xietee.service;

import com.xietee.dto.BookSaveDTO;
import com.xietee.dto.BookUpdateDTO;
import com.xietee.dto.StudentSaveDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AdminService 的纯单元测试：用两个内存版 Mapper 代替数据库，
 * 只验证业务逻辑（异常、参数映射、模糊搜索规则等）。
 */
class AdminServiceTest {

    private InMemoryStudentMapper studentMapper;
    private InMemoryBookMapper bookMapper;
    private InMemoryRecordMapper recordMapper;
    private AdminService service;

    @BeforeEach
    void setUp() {
        studentMapper = new InMemoryStudentMapper();
        bookMapper = new InMemoryBookMapper();
        recordMapper = new InMemoryRecordMapper();
        service = new AdminService(studentMapper, bookMapper, recordMapper);
    }

    private Student seedStudent(String studentID, String password, String name, String phoneNumber) {
        return studentMapper.seed(studentID, password, name, phoneNumber);
    }

    private Student seedDefaultStudent() {
        return seedStudent("202400010012", "password1", "张三", "13800000001");
    }

    private Book seedBook(String ISBN, String title, String author, boolean available) {
        return bookMapper.seed(ISBN, title, author, available);
    }

    private Book seedDefaultBook() {
        return seedBook("9787111000001", "Java编程思想", "张三", true);
    }

    // ================================================================ 学生

    @Test
    @DisplayName("findAllStudent：表为空时抛出 NO_DATA")
    void findAllStudentThrowsWhenEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findAllStudent());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findAllStudent：返回全部学生")
    void findAllStudentReturnsAllStudents() {
        seedDefaultStudent();
        seedStudent("202400010013", "password1", "李四", "13800000002");

        List<Student> students = service.findAllStudent();

        assertEquals(2, students.size());
        assertEquals(
                Set.of("202400010012", "202400010013"),
                students.stream().map(Student::getStudentID).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("findStudentById：命中时返回对应学生")
    void findStudentByIdReturnsStudent() {
        Student seeded = seedDefaultStudent();

        Student found = service.findStudentById(seeded.getId());

        assertEquals("张三", found.getName());
        assertEquals("202400010012", found.getStudentID());
    }

    @Test
    @DisplayName("findStudentById：不存在时异常信息带上 id")
    void findStudentByIdThrowsWhenMissing() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findStudentById(99L));
        assertEquals("未查找到学生 id: 99", ex.getMessage());
    }

    @Test
    @DisplayName("findStudentByStudentID：命中时返回对应学生")
    void findStudentByStudentIDReturnsStudent() {
        seedDefaultStudent();

        Student found = service.findStudentByStudentID("202400010012");

        assertEquals("张三", found.getName());
    }

    @Test
    @DisplayName("findStudentByStudentID：不存在时异常信息带上学号")
    void findStudentByStudentIDThrowsWhenMissing() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findStudentByStudentID("202499999999"));
        assertEquals("未查找到学生 学号: 202499999999", ex.getMessage());
    }

    @Test
    @DisplayName("findStudentByName：重名时返回所有学生")
    void findStudentByNameReturnsAllMatches() {
        seedStudent("202400010012", "password1", "张三", "13800000001");
        seedStudent("202400010013", "password1", "张三", "13800000002");
        seedStudent("202400010014", "password1", "李四", "13800000003");

        List<Student> students = service.findStudentByName("张三");

        assertEquals(2, students.size());
        assertTrue(students.stream().allMatch(s -> s.getName().equals("张三")));
    }

    @Test
    @DisplayName("findStudentByName：查不到时异常信息带上姓名")
    void findStudentByNameThrowsWhenMissing() {
        seedDefaultStudent();

        BusinessException ex = assertThrows(BusinessException.class, () -> service.findStudentByName("王五"));
        assertEquals("未查找到学生 姓名 王五", ex.getMessage());
    }

    @Test
    @DisplayName("findStudentByContractNumber：按手机号查询学生")
    void findStudentByContractNumberLooksUpByPhoneNumber() {
        seedDefaultStudent();

        Student found = service.findStudentByContractNumber("13800000001");

        assertEquals("202400010012", found.getStudentID());
    }

    @Test
    @DisplayName("findStudentByContractNumber：查不到时异常信息带上手机号")
    void findStudentByContractNumberThrowsWhenMissing() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findStudentByContractNumber("13800000099"));
        assertEquals("未查找到学生 电话号码 = 13800000099", ex.getMessage());
    }

    @Test
    @DisplayName("fuzzySearchStudent：关键词为空时返回全部学生")
    void fuzzySearchStudentWithBlankKeywordReturnsAll() {
        seedStudent("202400010012", "password1", "张三", "13800000001");
        seedStudent("202500020014", "password1", "李四", "13900000002");

        assertEquals(2, service.fuzzySearchStudent("").size());
    }

    @Test
    @DisplayName("fuzzySearchStudent：按学号模糊匹配")
    void fuzzySearchStudentMatchesStudentID() {
        seedStudent("202400010012", "password1", "张三", "13800000001");
        seedStudent("202500020014", "password1", "李四", "13900000002");

        List<Student> students = service.fuzzySearchStudent("2025");

        assertEquals(1, students.size());
        assertEquals("李四", students.getFirst().getName());
    }

    @Test
    @DisplayName("fuzzySearchStudent：按姓名模糊匹配")
    void fuzzySearchStudentMatchesName() {
        seedStudent("202400010012", "password1", "张三", "13800000001");
        seedStudent("202400010013", "password1", "李四", "13800000002");
        seedStudent("202400010014", "password1", "张小明", "13800000003");

        List<Student> students = service.fuzzySearchStudent("张");

        assertEquals(
                Set.of("张三", "张小明"),
                students.stream().map(Student::getName).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("fuzzySearchStudent：按手机号模糊匹配")
    void fuzzySearchStudentMatchesPhoneNumber() {
        seedStudent("202400010012", "password1", "张三", "13800000001");
        seedStudent("202500020014", "password1", "李四", "13900000002");

        List<Student> students = service.fuzzySearchStudent("139");

        assertEquals(1, students.size());
        assertEquals("202500020014", students.getFirst().getStudentID());
    }

    @Test
    @DisplayName("fuzzySearchStudent：没有匹配项时返回空列表（不抛异常）")
    void fuzzySearchStudentReturnsEmptyListWhenNothingMatches() {
        seedDefaultStudent();

        assertTrue(service.fuzzySearchStudent("不存在的人").isEmpty());
    }

    @Test
    @DisplayName("fuzzySearchStudent：表为空时沿用 findAllStudent 的 NO_DATA 异常")
    void fuzzySearchStudentThrowsWhenTableEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.fuzzySearchStudent(""));
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("insertStudent：把 DTO 的字段完整写入实体")
    void insertStudentMapsDtoFields() {
        StudentSaveDTO dto =
                new StudentSaveDTO("202400010016", "password1", "李四", "13700000003");

        assertEquals(1, service.insertStudent(dto));

        Student saved = service.findStudentByStudentID("202400010016");
        assertEquals("password1", saved.getPassword());
        assertEquals("李四", saved.getName());
        assertEquals("13700000003", saved.getPhoneNumber());
    }

    @Test
    @DisplayName("updateStudentById：同时更新密码和手机号")
    void updateStudentByIdChangesPasswordAndPhoneNumber() {
        Student seeded = seedDefaultStudent();
        StudentUpdateDTO dto = new StudentUpdateDTO("newPassword1", "13900000002");

        assertEquals(1, service.updateStudentById(seeded.getId(), dto));

        Student updated = service.findStudentById(seeded.getId());
        assertEquals("newPassword1", updated.getPassword());
        assertEquals("13900000002", updated.getPhoneNumber());
    }

    @Test
    @DisplayName("updateStudentById：学生不存在时抛出 UPDATE_FAIL")
    void updateStudentByIdThrowsWhenStudentMissing() {
        StudentUpdateDTO dto = new StudentUpdateDTO("newPassword1", "13900000002");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateStudentById(99L, dto));
        assertEquals("更新失败", ex.getMessage());
    }

    @Test
    @DisplayName("deleteStudentById：删除成功返回 1 且数据消失")
    void deleteStudentByIdRemovesRow() {
        Student seeded = seedDefaultStudent();

        assertEquals(1, service.deleteStudentById(seeded.getId()));
        assertEquals(0, studentMapper.size());
        assertThrows(BusinessException.class, () -> service.findStudentById(seeded.getId()));
    }

    @Test
    @DisplayName("deleteStudentById：id 不存在时返回 0")
    void deleteStudentByIdReturnsZeroWhenMissing() {
        seedDefaultStudent();

        assertEquals(0, service.deleteStudentById(99L));
        assertEquals(1, studentMapper.size());
    }

    @Test
    @DisplayName("existedStudentID：已存在返回 true")
    void existedStudentIDReturnsTrue() {
        seedDefaultStudent();

        assertTrue(service.existedStudentID("202400010012"));
    }

    @Test
    @DisplayName("existedStudentID：不存在返回 false")
    void existedStudentIDReturnsFalse() {
        seedDefaultStudent();

        assertFalse(service.existedStudentID("202400019999"));
    }

    @Test
    @DisplayName("existedPhoneNumber：已存在返回 true")
    void existedPhoneNumberReturnsTrue() {
        seedDefaultStudent();

        assertTrue(service.existedPhoneNumber("13800000001"));
    }

    @Test
    @DisplayName("existedPhoneNumber：不存在返回 false")
    void existedPhoneNumberReturnsFalse() {
        seedDefaultStudent();

        assertFalse(service.existedPhoneNumber("13800000099"));
    }

    // ================================================================ 书籍

    @Test
    @DisplayName("findAllBook：表为空时抛出 NO_DATA")
    void findAllBookThrowsWhenEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findAllBook());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findAllBook：返回全部书籍")
    void findAllBookReturnsAllBooks() {
        seedDefaultBook();
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        List<Book> books = service.findAllBook();

        assertEquals(2, books.size());
        assertEquals(
                Set.of("9787111000001", "9787121000002"),
                books.stream().map(Book::getISBN).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("findBookById：命中时返回对应书籍")
    void findBookByIdReturnsBook() {
        Book seeded = seedDefaultBook();

        Book found = service.findBookById(seeded.getId());

        assertEquals("Java编程思想", found.getTitle());
        assertEquals("9787111000001", found.getISBN());
    }

    @Test
    @DisplayName("findBookById：不存在时异常信息带上 id")
    void findBookByIdThrowsWhenMissing() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findBookById(99L));
        assertEquals("未查找到书籍 id: 99", ex.getMessage());
    }

    @Test
    @DisplayName("findBookByISBN：按 ISBN 查询，而不是按书名")
    void findBookByISBNLooksUpByISBN() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        List<Book> books = service.findBookByISBN("9787111000001");

        assertEquals(1, books.size());
        assertEquals("Java编程思想", books.getFirst().getTitle());
    }

    @Test
    @DisplayName("findBookByISBN：同一 ISBN 的多本副本都会返回")
    void findBookByISBNReturnsAllCopies() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000001", "Java编程思想", "张三", false);

        assertEquals(2, service.findBookByISBN("9787111000001").size());
    }

    @Test
    @DisplayName("findBookByISBN：查不到时异常信息带上 ISBN")
    void findBookByISBNThrowsWhenMissing() {
        seedDefaultBook();

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findBookByISBN("9787999999999"));
        assertEquals("未查找到书籍 ISBN: 9787999999999", ex.getMessage());
    }

    @Test
    @DisplayName("findBookByTitle：返回同名书籍")
    void findBookByTitleReturnsMatches() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000002", "Java编程思想", "李四", false);
        seedBook("9787121000003", "MySQL必知必会", "王五", true);

        List<Book> books = service.findBookByTitle("Java编程思想");

        assertEquals(2, books.size());
        assertTrue(books.stream().allMatch(b -> b.getTitle().equals("Java编程思想")));
    }

    @Test
    @DisplayName("findBookByTitle：查不到时异常信息带上书名")
    void findBookByTitleThrowsWhenMissing() {
        seedDefaultBook();

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findBookByTitle("不存在的书"));
        assertEquals("未查找到书籍 书名: 不存在的书", ex.getMessage());
    }

    @Test
    @DisplayName("findBookByAuthor：返回同一作者的全部书籍")
    void findBookByAuthorReturnsMatches() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000002", "Effective Java", "张三", false);
        seedBook("9787121000003", "MySQL必知必会", "李四", true);

        List<Book> books = service.findBookByAuthor("张三");

        assertEquals(2, books.size());
        assertTrue(books.stream().allMatch(b -> b.getAuthor().equals("张三")));
    }

    @Test
    @DisplayName("findBookByAuthor：查不到时异常信息带上作者")
    void findBookByAuthorThrowsWhenMissing() {
        seedDefaultBook();

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findBookByAuthor("不存在的作者"));
        assertEquals("未查找到书籍 书籍作者: 不存在的作者", ex.getMessage());
    }

    @Test
    @DisplayName("findBookByAvailable：返回可借 / 已借出的书籍")
    void findBookByAvailableFiltersByStatus() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        assertEquals(1, service.findBookByAvailable(true).size());
        assertEquals("9787111000001", service.findBookByAvailable(true).getFirst().getISBN());
        assertEquals(1, service.findBookByAvailable(false).size());
        assertEquals("9787121000002", service.findBookByAvailable(false).getFirst().getISBN());
    }

    @Test
    @DisplayName("findBookByAvailable：查不到时异常信息带上状态文案")
    void findBookByAvailableThrowsWhenMissing() {
        seedBook("9787111000001", "Java编程思想", "张三", false);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findBookByAvailable(true));
        assertEquals("未查找到书籍 是否借出: 是", ex.getMessage());
    }

    @Test
    @DisplayName("fuzzySearchBook：关键词为空时返回全部书籍")
    void fuzzySearchBookWithBlankKeywordReturnsAll() {
        seedDefaultBook();
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        assertEquals(2, service.fuzzySearchBook("").size());
    }

    @Test
    @DisplayName("fuzzySearchBook：按 ISBN / 书名 / 作者模糊匹配")
    void fuzzySearchBookMatchesAllFields() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        assertEquals("9787111000001", service.fuzzySearchBook("7111").getFirst().getISBN());
        assertEquals("9787121000002", service.fuzzySearchBook("MySQL").getFirst().getISBN());
        assertEquals("9787121000002", service.fuzzySearchBook("李四").getFirst().getISBN());
    }

    @Test
    @DisplayName("fuzzySearchBook：没有匹配项时返回空列表")
    void fuzzySearchBookReturnsEmptyListWhenNothingMatches() {
        seedDefaultBook();

        assertTrue(service.fuzzySearchBook("不存在").isEmpty());
    }

    @Test
    @DisplayName("fuzzySearchBook：表为空时沿用 findAllBook 的 NO_DATA 异常")
    void fuzzySearchBookThrowsWhenTableEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.fuzzySearchBook(""));
        assertEquals("还没有数据", ex.getMessage());
    }

    // ------------------------------------------------------------ 书籍汇总

    @Test
    @DisplayName("findAllBookWithCount：按 ISBN 汇总副本数")
    void findAllBookWithCountGroupsByISBN() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000001", "Java编程思想", "张三", false);
        seedBook("9787121000002", "MySQL必知必会", "李四", true);

        List<Book> books = service.findAllBookWithCount();

        assertEquals(2, books.size());
        Book java = books.stream()
                .filter(book -> book.getISBN().equals("9787111000001"))
                .findFirst().orElseThrow();
        assertEquals(2, java.getTotalCount());
        assertEquals(1, java.getAvailableCount());
        assertEquals("Java编程思想", java.getTitle());
    }

    @Test
    @DisplayName("findAllBookWithCount：表为空时抛出 NO_DATA")
    void findAllBookWithCountThrowsWhenEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findAllBookWithCount());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findAllAvailableBookWithCount：只返回有可借副本的 ISBN")
    void findAllAvailableBookWithCountFiltersGroups() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000001", "Java编程思想", "张三", false);
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        List<Book> books = service.findAllAvailableBookWithCount();

        assertEquals(1, books.size());
        assertEquals("9787111000001", books.getFirst().getISBN());
        assertEquals(2, books.getFirst().getTotalCount());
        assertEquals(1, books.getFirst().getAvailableCount());
    }

    @Test
    @DisplayName("findAllAvailableBookWithCount：全部借出时抛出 NO_DATA")
    void findAllAvailableBookWithCountThrowsWhenNoAvailable() {
        seedBook("9787111000001", "Java编程思想", "张三", false);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findAllAvailableBookWithCount());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("fuzzySearchBookWithCount：关键词为空时返回全部汇总")
    void fuzzySearchBookWithCountWithBlankKeywordReturnsAllGroups() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787121000002", "MySQL必知必会", "李四", false);

        assertEquals(2, service.fuzzySearchBookWithCount("").size());
    }

    @Test
    @DisplayName("fuzzySearchBookWithCount：按关键词汇总匹配的 ISBN")
    void fuzzySearchBookWithCountMatchesKeyword() {
        seedBook("9787111000001", "Java编程思想", "张三", true);
        seedBook("9787111000001", "Java编程思想", "张三", false);
        seedBook("9787121000002", "MySQL必知必会", "李四", true);

        List<Book> books = service.fuzzySearchBookWithCount("Java");

        assertEquals(1, books.size());
        assertEquals(2, books.getFirst().getTotalCount());
        assertEquals(1, books.getFirst().getAvailableCount());
    }

    // ------------------------------------------------------------ 书籍增删改

    @Test
    @DisplayName("insertBook：把 DTO 字段写入实体")
    void insertBookMapsDtoFields() {
        BookSaveDTO dto = new BookSaveDTO("9787111000009", "重构", "Martin Fowler", false);

        assertEquals(1, service.insertBook(dto));

        Book saved = bookMapper.findByISBN("9787111000009").getFirst();
        assertEquals("重构", saved.getTitle());
        assertEquals("Martin Fowler", saved.getAuthor());
        assertFalse(saved.isAvailable());
    }

    @Test
    @DisplayName("updateBookById：更新书名和作者，不改变可借状态")
    void updateBookByIdChangesEditableColumns() {
        Book seeded = seedDefaultBook();
        BookUpdateDTO dto = new BookUpdateDTO("Effective Java", "李四");

        assertEquals(1, service.updateBookById(seeded.getId(), dto));

        Book updated = service.findBookById(seeded.getId());
        assertEquals("Effective Java", updated.getTitle());
        assertEquals("李四", updated.getAuthor());
        assertTrue(updated.isAvailable(), "管理员更新书名/作者不应改变可借状态");
        assertEquals("9787111000001", updated.getISBN());
    }

    @Test
    @DisplayName("updateBookById：已借出的书籍更新后仍然不可借")
    void updateBookByIdKeepsUnavailableState() {
        Book seeded = seedBook("9787111000001", "Java编程思想", "张三", false);
        BookUpdateDTO dto = new BookUpdateDTO("Effective Java", "李四");

        assertEquals(1, service.updateBookById(seeded.getId(), dto));

        Book updated = service.findBookById(seeded.getId());
        assertEquals("Effective Java", updated.getTitle());
        assertFalse(updated.isAvailable());
    }

    @Test
    @DisplayName("updateBookById：书籍不存在时抛出 UPDATE_FAIL")
    void updateBookByIdThrowsWhenMissing() {
        BookUpdateDTO dto = new BookUpdateDTO("Effective Java", "李四");

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.updateBookById(99L, dto));
        assertEquals("更新失败", ex.getMessage());
    }

    @Test
    @DisplayName("deleteBookById：可借书籍可以删除")
    void deleteBookByIdRemovesAvailableBook() {
        Book seeded = seedDefaultBook();

        assertEquals(1, service.deleteBookById(seeded.getId()));
        assertThrows(BusinessException.class, () -> service.findBookById(seeded.getId()));
    }

    @Test
    @DisplayName("deleteBookById：未归还的书籍不允许删除")
    void deleteBookByIdRejectsUnreturnedBook() {
        Book seeded = seedBook("9787111000001", "Java编程思想", "张三", false);

        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.deleteBookById(seeded.getId()));
        assertEquals("该书籍还未归还", ex.getMessage());
        assertEquals(1, bookMapper.size());
    }

    @Test
    @DisplayName("deleteBookById：书籍不存在时抛出 BOOK_NOT_FOUND")
    void deleteBookByIdThrowsWhenMissing() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.deleteBookById(99L));
        assertEquals("未查找到书籍 id: 99", ex.getMessage());
    }

    // ------------------------------------------------------------ 借阅记录

    @Test
    @DisplayName("findAllRecord：表为空时抛出 NO_DATA")
    void findAllRecordThrowsWhenEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findAllRecord());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findAllRecord：返回全部借阅记录")
    void findAllRecordReturnsAllRecords() {
        recordMapper.seedBorrowed(1L, "202400010012", "9787111000001");

        assertEquals(1, service.findAllRecord().size());
    }

    @Test
    @DisplayName("findUnReturnedRecord：表为空时抛出 NO_DATA")
    void findUnReturnedRecordThrowsWhenEmpty() {
        BusinessException ex = assertThrows(
                BusinessException.class, () -> service.findUnReturnedRecord());
        assertEquals("还没有数据", ex.getMessage());
    }

    @Test
    @DisplayName("findUnReturnedRecord：只返回未归还记录")
    void findUnReturnedRecordFiltersReturnedRecords() {
        recordMapper.seedBorrowed(1L, "202400010012", "9787111000001");
        recordMapper.seed(2L, "202400010012", "9787121000002",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        List<Record> records = service.findUnReturnedRecord();

        assertEquals(1, records.size());
        assertEquals(1L, records.getFirst().getBookID());
    }
}
