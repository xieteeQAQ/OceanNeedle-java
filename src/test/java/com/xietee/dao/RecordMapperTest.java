package com.xietee.dao;

import com.xietee.entity.Record;
import com.xietee.support.H2Database;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RecordMapper 的集成测试：真实执行 RecordMapper.xml 里的 SQL（H2 内存库）。
 */
class RecordMapperTest {

    private static final String STUDENT_ID = "202400010012";
    private static final String OTHER_STUDENT_ID = "202400019999";
    private static final String ISBN_JAVA = "9787111000001";
    private static final String ISBN_MYSQL = "9787121000002";

    private SqlSession session;
    private RecordMapper mapper;

    @BeforeEach
    void setUp() {
        H2Database.resetSchema();
        session = H2Database.factory().openSession(true);
        mapper = session.getMapper(RecordMapper.class);
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    private Record insert(long bookID, String studentID, String ISBN) {
        int rows = mapper.insertRecord(new Record(null, bookID, studentID, ISBN, null, null));
        assertEquals(1, rows);
        return mapper.findAll().stream()
                .max(Comparator.comparing(Record::getId))
                .orElseThrow();
    }

    @Test
    @DisplayName("findAll：空表返回空列表")
    void findAllReturnsEmptyList() {
        assertTrue(mapper.findAll().isEmpty());
    }

    @Test
    @DisplayName("insertRecord：自动写入借出时间，归还时间为空")
    void insertRecordSetsBorrowTime() {
        Record saved = insert(1L, STUDENT_ID, ISBN_JAVA);

        assertNotNull(saved.getBorrowTime());
        assertNull(saved.getReturnTime());
        assertEquals(1L, saved.getBookID());
        assertEquals(STUDENT_ID, saved.getStudentID());
        assertEquals(ISBN_JAVA, saved.getISBN());
    }

    @Test
    @DisplayName("findById：命中 / 缺失")
    void findById() {
        Record saved = insert(1L, STUDENT_ID, ISBN_JAVA);

        assertEquals(ISBN_JAVA, mapper.findById(saved.getId()).orElseThrow().getISBN());
        assertTrue(mapper.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByBookID：返回同一本书的全部借阅记录")
    void findByBookID() {
        insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(1L, OTHER_STUDENT_ID, ISBN_JAVA);
        insert(2L, STUDENT_ID, ISBN_MYSQL);

        List<Record> records = mapper.findByBookID(1L);

        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(record -> record.getBookID().equals(1L)));
        assertTrue(mapper.findByBookID(99L).isEmpty());
    }

    @Test
    @DisplayName("findByStudentID：返回该学生全部记录")
    void findByStudentID() {
        insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(2L, STUDENT_ID, ISBN_MYSQL);
        insert(3L, OTHER_STUDENT_ID, ISBN_JAVA);

        assertEquals(2, mapper.findByStudentID(STUDENT_ID).size());
    }

    @Test
    @DisplayName("findByISBN：返回同一 ISBN 的全部借阅记录")
    void findByISBN() {
        insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(2L, OTHER_STUDENT_ID, ISBN_JAVA);
        insert(3L, STUDENT_ID, ISBN_MYSQL);

        assertEquals(2, mapper.findByISBN(ISBN_JAVA).size());
    }

    @Test
    @DisplayName("findUnReturned：只返回未归还记录")
    void findUnReturned() {
        Record first = insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(2L, STUDENT_ID, ISBN_MYSQL);
        mapper.updateReturnTimeById(first.getId());

        List<Record> records = mapper.findUnReturned();

        assertEquals(1, records.size());
        assertEquals(2L, records.getFirst().getBookID());
    }

    @Test
    @DisplayName("findUnReturnedByStudentID：按学生过滤未归还记录")
    void findUnReturnedByStudentID() {
        insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(2L, STUDENT_ID, ISBN_MYSQL);
        insert(3L, OTHER_STUDENT_ID, ISBN_JAVA);

        assertEquals(2, mapper.findUnReturnedByStudentID(STUDENT_ID).size());
        assertEquals(1, mapper.findUnReturnedByStudentID(OTHER_STUDENT_ID).size());
    }

    @Test
    @DisplayName("findUnReturnedByStudentIDAndISBN：同时按学生和 ISBN 过滤")
    void findUnReturnedByStudentIDAndISBN() {
        insert(1L, STUDENT_ID, ISBN_JAVA);
        insert(2L, STUDENT_ID, ISBN_MYSQL);

        assertEquals(1, mapper.findUnReturnedByStudentIDAndISBN(STUDENT_ID, ISBN_JAVA).size());
        assertTrue(mapper.findUnReturnedByStudentIDAndISBN(STUDENT_ID, "9787999999999").isEmpty());
    }

    @Test
    @DisplayName("fuzzySearch：按 bookID / 学号 / ISBN 模糊匹配")
    void fuzzySearch() {
        insert(123L, STUDENT_ID, ISBN_JAVA);

        assertEquals(1, mapper.fuzzySearch("123").size());
        assertEquals(1, mapper.fuzzySearch("010012").size());
        assertEquals(1, mapper.fuzzySearch("7111").size());
        assertTrue(mapper.fuzzySearch("不存在").isEmpty());
    }

    @Test
    @DisplayName("updateReturnTimeById：写入归还时间")
    void updateReturnTimeById() {
        Record saved = insert(1L, STUDENT_ID, ISBN_JAVA);

        assertEquals(1, mapper.updateReturnTimeById(saved.getId()));

        Record updated = mapper.findById(saved.getId()).orElseThrow();
        assertNotNull(updated.getReturnTime());
    }

    @Test
    @DisplayName("updateReturnTimeById：id 不存在返回 0")
    void updateReturnTimeByIdReturnsZeroWhenMissing() {
        assertEquals(0, mapper.updateReturnTimeById(99L));
    }

    @Test
    @DisplayName("deleteById：删除成功返回 1，再次删除返回 0")
    void deleteByIdRemovesRow() {
        Record saved = insert(1L, STUDENT_ID, ISBN_JAVA);

        assertEquals(1, mapper.deleteById(saved.getId()));
        assertTrue(mapper.findById(saved.getId()).isEmpty());
        assertEquals(0, mapper.deleteById(saved.getId()));
    }
}
