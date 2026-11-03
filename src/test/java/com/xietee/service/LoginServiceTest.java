package com.xietee.service;

import com.alibaba.fastjson2.JSONObject;
import com.xietee.cli.UserType;
import com.xietee.entity.Admin;
import com.xietee.entity.Student;
import com.xietee.exception.LoginException;
import com.xietee.support.InMemoryAdminMapper;
import com.xietee.support.InMemoryStudentMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.xietee.exception.BusinessExceptionEnum.LOGIN_SESSION_EXPIRED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginService 的测试：账号查询 + user.json 登录会话。
 *
 * <p>这些用例会读写 src/main/resources/user.json，
 * 因此在 @BeforeEach 备份、@AfterEach 还原原文件内容。</p>
 */
class LoginServiceTest {

    private static final Path USER_JSON = Path.of("src", "main", "resources", "user.json");
    private static final String STUDENT_ID = "202400010012";
    private static final String ADMIN_ID = "000000000001";

    private InMemoryStudentMapper studentMapper;
    private InMemoryAdminMapper adminMapper;
    private LoginService loginService;
    private byte[] originalUserJson;

    @BeforeEach
    void setUp() throws IOException {
        studentMapper = new InMemoryStudentMapper();
        adminMapper = new InMemoryAdminMapper();
        loginService = new LoginService(studentMapper, adminMapper);
        originalUserJson = Files.readAllBytes(USER_JSON);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (originalUserJson != null) {
            Files.write(USER_JSON, originalUserJson);
        }
    }

    private void writeUserJson(String content) throws IOException {
        Files.writeString(USER_JSON, content, StandardCharsets.UTF_8);
    }

    private JSONObject readUserJson() throws IOException {
        return JSONObject.parseObject(Files.readString(USER_JSON, StandardCharsets.UTF_8));
    }

    // ------------------------------------------------------------ 账号查询

    @Test
    @DisplayName("LoginFindStudentByStudentID：命中 / 缺失")
    void findStudentForLogin() {
        studentMapper.seed(STUDENT_ID, "password1", "张三", "13800000001");

        Optional<Student> found = loginService.LoginFindStudentByStudentID(STUDENT_ID);

        assertTrue(found.isPresent());
        assertEquals("张三", found.get().getName());
        assertTrue(loginService.LoginFindStudentByStudentID("202499999999").isEmpty());
    }

    @Test
    @DisplayName("LoginFindAdminByAdminID：命中 / 缺失")
    void findAdminForLogin() {
        adminMapper.seed(ADMIN_ID, "password1", "13800000002");

        Optional<Admin> found = loginService.LoginFindAdminByAdminID(ADMIN_ID);

        assertTrue(found.isPresent());
        assertEquals(ADMIN_ID, found.get().getAdminID());
        assertTrue(loginService.LoginFindAdminByAdminID("000000000099").isEmpty());
    }

    // ------------------------------------------------------------ 会话读取

    @Test
    @DisplayName("getUserFromJSON：读取有效的登录会话")
    void getUserFromJsonReturnsSession() throws IOException {
        writeUserJson("""
                {
                  "name": "张三",
                  "userType": "STUDENT",
                  "id": "202400010012"
                }
                """);

        JSONObject user = LoginService.getUserFromJSON();

        assertEquals("STUDENT", user.getString("userType"));
        assertEquals("202400010012", user.getString("id"));
        assertEquals("张三", user.getString("name"));
    }

    @Test
    @DisplayName("checkLoginSessionStatus：有效会话不抛异常")
    void checkLoginSessionStatusAcceptsValidSession() throws IOException {
        writeUserJson("""
                {
                  "name": "",
                  "userType": "ADMIN",
                  "id": "000000000001"
                }
                """);

        assertDoesNotThrow(LoginService::checkLoginSessionStatus);
    }

    @Test
    @DisplayName("checkLoginSessionStatus：内容为空时重置为 NULL 会话并抛出登录失效")
    void checkLoginSessionStatusResetsBlankSession() throws IOException {
        writeUserJson("");

        LoginException ex = assertThrows(LoginException.class, LoginService::checkLoginSessionStatus);

        assertEquals(LOGIN_SESSION_EXPIRED.getCode(), ex.getCode());
        assertEquals(LOGIN_SESSION_EXPIRED.getMessage(), ex.getMessage());
        assertEquals(UserType.NULL.getString(), readUserJson().getString("userType"));
    }

    @Test
    @DisplayName("checkLoginSessionStatus：缺少字段时重置为 NULL 会话")
    void checkLoginSessionStatusResetsIncompleteSession() throws IOException {
        writeUserJson("""
                {
                  "name": "张三"
                }
                """);

        assertThrows(LoginException.class, LoginService::checkLoginSessionStatus);

        JSONObject user = readUserJson();
        assertEquals("NULL", user.getString("userType"));
        assertEquals("", user.getString("id"));
        assertEquals("", user.getString("name"));
    }

    @Test
    @DisplayName("getUserFromJSON：会话失效时抛出 LoginException")
    void getUserFromJsonThrowsWhenSessionExpired() throws IOException {
        writeUserJson("{}");

        assertThrows(LoginException.class, LoginService::getUserFromJSON);
    }

    // ------------------------------------------------------------ 会话写入

    @Test
    @DisplayName("StudentService.writeToJSON：写入学生会话")
    void writeStudentSession() throws IOException {
        Student student = new Student(1L, STUDENT_ID, "password1", "张三", "13800000001");

        StudentService.writeToJSON(student);

        JSONObject user = readUserJson();
        assertEquals("STUDENT", user.getString("userType"));
        assertEquals(STUDENT_ID, user.getString("id"));
        assertEquals("张三", user.getString("name"));
    }

    @Test
    @DisplayName("AdminService.writeToJSON：写入管理员会话")
    void writeAdminSession() throws IOException {
        Admin admin = new Admin(1L, ADMIN_ID, "password1", "13800000002");

        AdminService.writeToJSON(admin);

        JSONObject user = readUserJson();
        assertEquals("ADMIN", user.getString("userType"));
        assertEquals(ADMIN_ID, user.getString("id"));
        assertEquals("", user.getString("name"));
    }
}
