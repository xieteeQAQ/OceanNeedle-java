package com.xietee.dao;

import com.xietee.entity.Admin;
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
 * AdminMapper 的集成测试：真实执行 AdminMapper.xml 里的 SQL（H2 内存库）。
 */
class AdminMapperTest {

    private static final String ADMIN_ID = "000000000001";

    private SqlSession session;
    private AdminMapper mapper;

    @BeforeEach
    void setUp() {
        H2Database.resetSchema();
        session = H2Database.factory().openSession(true);
        mapper = session.getMapper(AdminMapper.class);
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    private Admin insert(String adminID, String phoneNumber) {
        int rows = mapper.insertAdmin(new Admin(null, adminID, "password1", phoneNumber));
        assertEquals(1, rows);
        return mapper.findByAdminID(adminID).orElseThrow();
    }

    @Test
    @DisplayName("findAll：空表返回空列表")
    void findAllReturnsEmptyList() {
        assertTrue(mapper.findAll().isEmpty());
    }

    @Test
    @DisplayName("insertAdmin + findById：字段完整映射")
    void insertAndFindById() {
        Admin saved = insert(ADMIN_ID, "13800000001");

        Admin found = mapper.findById(saved.getId()).orElseThrow();

        assertEquals(ADMIN_ID, found.getAdminID());
        assertEquals("password1", found.getPassword());
        assertEquals("13800000001", found.getPhoneNumber());
    }

    @Test
    @DisplayName("findAll：返回全部管理员")
    void findAllReturnsAllRows() {
        insert(ADMIN_ID, "13800000001");
        insert("000000000002", "13800000002");

        List<Admin> admins = mapper.findAll();

        assertEquals(2, admins.size());
        assertEquals(
                Set.of(ADMIN_ID, "000000000002"),
                admins.stream().map(Admin::getAdminID).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("findById：不存在返回空 Optional")
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(mapper.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByAdminID：命中 / 缺失")
    void findByAdminID() {
        insert(ADMIN_ID, "13800000001");

        assertEquals(ADMIN_ID, mapper.findByAdminID(ADMIN_ID).orElseThrow().getAdminID());
        assertTrue(mapper.findByAdminID("000000000099").isEmpty());
    }

    @Test
    @DisplayName("findByPhoneNumber：命中 / 缺失")
    void findByPhoneNumber() {
        insert(ADMIN_ID, "13800000001");

        assertEquals(ADMIN_ID, mapper.findByPhoneNumber("13800000001").orElseThrow().getAdminID());
        assertTrue(mapper.findByPhoneNumber("13800000099").isEmpty());
    }

    @Test
    @DisplayName("updateById：更新密码和手机号")
    void updateByIdChangesPasswordAndPhoneNumber() {
        Admin saved = insert(ADMIN_ID, "13800000001");
        saved.setPassword("newPassword1");
        saved.setPhoneNumber("13900000002");

        assertEquals(1, mapper.updateById(saved));

        Admin updated = mapper.findById(saved.getId()).orElseThrow();
        assertEquals("newPassword1", updated.getPassword());
        assertEquals("13900000002", updated.getPhoneNumber());
        assertEquals(ADMIN_ID, updated.getAdminID());
    }

    @Test
    @DisplayName("updateById：id 不存在返回 0")
    void updateByIdReturnsZeroWhenMissing() {
        Admin ghost = new Admin(99L, "000000000099", "password1", "13800000099");

        assertEquals(0, mapper.updateById(ghost));
    }

    @Test
    @DisplayName("deleteById：删除成功返回 1，再次删除返回 0")
    void deleteByIdRemovesRow() {
        Admin saved = insert(ADMIN_ID, "13800000001");

        assertEquals(1, mapper.deleteById(saved.getId()));
        assertTrue(mapper.findById(saved.getId()).isEmpty());
        assertEquals(0, mapper.deleteById(saved.getId()));
    }

    @Test
    @DisplayName("insertAdmin：重复 adminID 抛出 PersistenceException")
    void insertDuplicateAdminIDFails() {
        insert(ADMIN_ID, "13800000001");

        assertThrows(PersistenceException.class,
                () -> insert(ADMIN_ID, "13800000002"));
    }

    @Test
    @DisplayName("insertAdmin：重复手机号抛出 PersistenceException")
    void insertDuplicatePhoneNumberFails() {
        insert(ADMIN_ID, "13800000001");

        assertThrows(PersistenceException.class,
                () -> insert("000000000002", "13800000001"));
    }
}
