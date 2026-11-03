package com.xietee.dao;

import com.xietee.entity.Student;
import com.xietee.support.H2Database;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StudentMapper 的集成测试：真实执行 StudentMapper.xml 里的 SQL，
 * 数据库使用 H2 内存库（MySQL 兼容模式），不需要本地 MySQL。
 */
class StudentMapperTest {

    private SqlSession session;
    private StudentMapper mapper;

    @BeforeEach
    void setUp() {
        H2Database.resetSchema();
        session = H2Database.factory().openSession(true);
        mapper = session.getMapper(StudentMapper.class);
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    /** 插入一条学生并返回数据库中的完整记录（含自增 id）。 */
    private Student insert(String studentID, String name, String phoneNumber) {
        int rows = mapper.insertStudent(new Student(null, studentID, "password1", name, phoneNumber));
        assertEquals(1, rows);
        return mapper.findByStudentID(studentID).orElseThrow();
    }

    // ---------------------------------------------------------------- 查询

    @Test
    @DisplayName("findAll：空表返回空列表")
    void findAllReturnsEmptyList() {
        assertTrue(mapper.findAll().isEmpty());
    }

    @Test
    @DisplayName("findAll：返回全部学生")
    void findAllReturnsAllRows() {
        insert("202400010012", "张三", "13800000001");
        insert("202400010013", "李四", "13800000002");

        List<Student> students = mapper.findAll();

        assertEquals(2, students.size());
        assertEquals(
                Set.of("202400010012", "202400010013"),
                students.stream().map(Student::getStudentID).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("findById：字段完整映射（含驼峰列名 studentID / phoneNumber）")
    void findByIdMapsAllColumns() {
        Student saved = insert("202400010012", "张三", "13800000001");

        Student found = mapper.findById(saved.getId()).orElseThrow();

        assertEquals("202400010012", found.getStudentID());
        assertEquals("张三", found.getName());
        assertEquals("password1", found.getPassword());
        assertEquals("13800000001", found.getPhoneNumber());
    }

    @Test
    @DisplayName("findById：不存在的 id 返回空 Optional")
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(mapper.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("findByStudentID：命中时返回学生")
    void findByStudentIDReturnsStudent() {
        insert("202400010012", "张三", "13800000001");

        assertEquals("张三", mapper.findByStudentID("202400010012").orElseThrow().getName());
    }

    @Test
    @DisplayName("findByStudentID：不存在返回空 Optional")
    void findByStudentIDReturnsEmptyWhenMissing() {
        assertTrue(mapper.findByStudentID("202499999999").isEmpty());
    }

    @Test
    @DisplayName("findByName：重名返回多条，且是精确匹配")
    void findByNameReturnsExactMatches() {
        insert("202400010012", "张三", "13800000001");
        insert("202400010013", "张三", "13800000002");
        insert("202400010014", "李四", "13800000003");

        List<Student> matches = mapper.findByName("张三");

        assertEquals(2, matches.size());
        assertTrue(matches.stream().allMatch(s -> s.getName().equals("张三")));
        assertTrue(mapper.findByName("张").isEmpty(), "findByName 目前是精确匹配，不是模糊匹配");
    }

    @Test
    @DisplayName("findByPhoneNumber：按手机号命中")
    void findByPhoneNumberReturnsStudent() {
        insert("202400010012", "张三", "13800000001");

        Student found = mapper.findByPhoneNumber("13800000001").orElseThrow();

        assertEquals("202400010012", found.getStudentID());
    }

    @Test
    @DisplayName("findByPhoneNumber：不存在返回空 Optional")
    void findByPhoneNumberReturnsEmptyWhenMissing() {
        assertTrue(mapper.findByPhoneNumber("13800000099").isEmpty());
    }

    // ---------------------------------------------------------------- 更新

    @Test
    @DisplayName("updateById：同时更新密码和手机号")
    void updateByIdChangesPasswordAndPhoneNumber() {
        Student saved = insert("202400010012", "张三", "13800000001");
        saved.setPassword("newPassword1");
        saved.setPhoneNumber("13900000002");

        assertEquals(1, mapper.updateById(saved));

        Student updated = mapper.findById(saved.getId()).orElseThrow();
        assertEquals("newPassword1", updated.getPassword());
        assertEquals("13900000002", updated.getPhoneNumber());
        assertEquals("202400010012", updated.getStudentID(), "updateById 不应改动学号");
        assertEquals("张三", updated.getName(), "updateById 不应改动姓名");
    }

    @Test
    @DisplayName("updateById：id 不存在时返回 0")
    void updateByIdReturnsZeroWhenMissing() {
        Student ghost = new Student(999L, "202499999999", "password1", "张三", "13800000099");

        assertEquals(0, mapper.updateById(ghost));
    }

    // ---------------------------------------------------------------- 删除

    @Test
    @DisplayName("deleteById：删除成功返回 1，再次删除返回 0")
    void deleteByIdRemovesRow() {
        Student saved = insert("202400010012", "张三", "13800000001");

        assertEquals(1, mapper.deleteById(saved.getId()));
        assertTrue(mapper.findById(saved.getId()).isEmpty());
        assertEquals(0, mapper.deleteById(saved.getId()));
    }

    // ------------------------------------------------------------ 唯一约束

    @Test
    @DisplayName("insertStudent：重复学号抛出 PersistenceException")
    void insertDuplicateStudentIDFails() {
        insert("202400010012", "张三", "13800000001");

        assertThrows(PersistenceException.class, () -> insert("202400010012", "李四", "13800000002"));
    }

    @Test
    @DisplayName("insertStudent：重复手机号抛出 PersistenceException")
    void insertDuplicatePhoneNumberFails() {
        insert("202400010012", "张三", "13800000001");

        assertThrows(PersistenceException.class, () -> insert("202400010013", "李四", "13800000001"));
    }
}
